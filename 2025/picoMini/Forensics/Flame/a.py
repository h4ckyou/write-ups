#!/usr/bin/env python3
"""
ocr_tool.py

Usage:
  python3 ocr_tool.py --input path/to/image_or_folder_or.pdf [--lang eng] [--out out.txt] [--use-easyocr]

Examples:
  python3 ocr_tool.py --input screenshot.png
  python3 ocr_tool.py --input ./images_dir --out results.txt
  python3 ocr_tool.py --input file.pdf --lang eng+spa
"""

import sys
import os
import argparse
from PIL import Image
import numpy as np
import pytesseract
import cv2

# optional
try:
    from pdf2image import convert_from_path
    PDF2IMAGE_AVAILABLE = True
except Exception:
    PDF2IMAGE_AVAILABLE = False

try:
    import easyocr
    EASYOCR_AVAILABLE = True
except Exception:
    EASYOCR_AVAILABLE = False

# --- Preprocessing helpers ---
def load_image(path):
    return cv2.imread(path, cv2.IMREAD_UNCHANGED)

def to_grayscale(img):
    if len(img.shape) == 3:
        return cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    return img

def denoise(img):
    return cv2.fastNlMeansDenoising(img, None, 10, 7, 21)

def threshold_adaptive(img):
    return cv2.adaptiveThreshold(img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                 cv2.THRESH_BINARY, 31, 2)

def resize_if_large(img, max_dim=2000):
    h, w = img.shape[:2]
    scale = 1.0
    if max(h, w) > max_dim:
        scale = max_dim / float(max(h, w))
        img = cv2.resize(img, (int(w*scale), int(h*scale)), interpolation=cv2.INTER_AREA)
    return img, scale

def deskew(img):
    # simple deskew using moments / minAreaRect on edges
    coords = np.column_stack(np.where(img > 0))
    if coords.size == 0:
        return img
    rect = cv2.minAreaRect(coords)
    angle = rect[-1]
    if angle < -45:
        angle = -(90 + angle)
    else:
        angle = -angle
    (h, w) = img.shape[:2]
    M = cv2.getRotationMatrix2D((w//2, h//2), angle, 1.0)
    rotated = cv2.warpAffine(img, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)
    return rotated

def preprocess_for_tesseract(img):
    gray = to_grayscale(img)
    gray = denoise(gray)
    gray = resize_if_large(gray)
    if isinstance(gray, tuple):
        gray, _s = gray
    # after resize variable may be tuple or int; ensure single image
    if type(gray) is tuple:
        gray = gray[0]
    # deskew & threshold
    desk = deskew(gray)
    thr = threshold_adaptive(desk)
    return thr

# --- OCR functions ---
def ocr_with_tesseract_pil(pil_image, lang="eng"):
    # PIL image -> tesseract
    config = "--psm 3"  # automatic page segmentation
    text = pytesseract.image_to_string(pil_image, lang=lang, config=config)
    return text

def ocr_with_tesseract_cv(img_cv, lang="eng"):
    # img_cv expected as grayscale or cv2 image
    processed = preprocess_for_tesseract(img_cv)
    pil = Image.fromarray(processed)
    return ocr_with_tesseract_pil(pil, lang=lang)

def ocr_with_easyocr(img_path_or_cv, languages=["en"]):
    if not EASYOCR_AVAILABLE:
        return ""
    # easyocr accepts path or numpy array (BGR or gray)
    if isinstance(img_path_or_cv, np.ndarray):
        arr = img_path_or_cv
    else:
        arr = cv2.imread(img_path_or_cv)
    reader = easyocr.Reader(languages, gpu=False)  # set gpu=True if you have CUDA + supported easyocr
    results = reader.readtext(arr, detail=0)
    return "\n".join(results)

# --- Helpers to read PDF pages to images ---
def pdf_to_images(pdf_path, dpi=200):
    if not PDF2IMAGE_AVAILABLE:
        raise RuntimeError("pdf2image not installed or poppler not available.")
    pages = convert_from_path(pdf_path, dpi=dpi)
    pil_images = pages
    return pil_images

# --- Main driver ---
def ocr_file(path, lang="eng", prefer_easyocr=False):
    ext = os.path.splitext(path)[1].lower()
    results = []
    if ext in [".pdf"]:
        if not PDF2IMAGE_AVAILABLE:
            raise RuntimeError("pdf2image/poppler not available. Install with: pip install pdf2image and system poppler.")
        pages = pdf_to_images(path)
        for i, pil in enumerate(pages):
            print(f"[PDF] page {i+1}/{len(pages)} -> OCRing ...")
            # convert pil to cv2
            cv_img = cv2.cvtColor(np.array(pil), cv2.COLOR_RGB2BGR)
            if prefer_easyocr and EASYOCR_AVAILABLE:
                text = ocr_with_easyocr(cv_img, languages=lang.split('+'))
            else:
                text = ocr_with_tesseract_cv(cv_img, lang=lang)
            results.append((path, i+1, text))
    else:
        img_cv = load_image(path)
        if img_cv is None:
            raise RuntimeError(f"Failed to read image: {path}")
        if prefer_easyocr and EASYOCR_AVAILABLE:
            text = ocr_with_easyocr(img_cv, languages=lang.split('+'))
        else:
            text = ocr_with_tesseract_cv(img_cv, lang=lang)
        results.append((path, None, text))
    return results

def process_input(input_path, lang="eng", out_file=None, use_easyocr=False):
    out_lines = []
    if os.path.isdir(input_path):
        for root, _, files in os.walk(input_path):
            for fn in sorted(files):
                if fn.lower().endswith(('.png','.jpg','.jpeg','.bmp','.tiff','.webp','.pdf')):
                    full = os.path.join(root, fn)
                    try:
                        res = ocr_file(full, lang=lang, prefer_easyocr=use_easyocr)
                        for (p, page, text) in res:
                            header = f"--- {p}" + (f" [page {page}]" if page else "")
                            out_lines.append(header)
                            out_lines.append(text.strip())
                            out_lines.append("\n")
                            print(header)
                    except Exception as e:
                        print("Error OCRing", full, e, file=sys.stderr)
    else:
        res = ocr_file(input_path, lang=lang, prefer_easyocr=use_easyocr)
        for (p, page, text) in res:
            header = f"--- {p}" + (f" [page {page}]" if page else "")
            out_lines.append(header)
            out_lines.append(text.strip())
            out_lines.append("\n")
            print(header)
    out_text = "\n".join(out_lines)
    if out_file:
        with open(out_file, "w", encoding="utf-8") as f:
            f.write(out_text)
        print(f"[saved] {out_file}")
    else:
        print("\n[RESULT]\n")
        print(out_text)
    return out_text

# --- CLI ---
def main_cli():
    parser = argparse.ArgumentParser(description="OCR tool using Tesseract + OpenCV (fallback EasyOCR).")
    parser.add_argument("--input", "-i", required=True, help="Image/PDF file or folder")
    parser.add_argument("--lang", "-l", default="eng", help="Tesseract language(s). e.g. eng or eng+spa")
    parser.add_argument("--out", "-o", help="Output text file (optional)")
    parser.add_argument("--use-easyocr", action="store_true", help="Prefer EasyOCR (if installed) instead of Tesseract")
    args = parser.parse_args()
    process_input(args.input, lang=args.lang, out_file=args.out, use_easyocr=args.use_easyocr)

if __name__ == "__main__":
    main_cli()
p