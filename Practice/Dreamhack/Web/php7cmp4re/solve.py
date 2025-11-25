from PIL import Image
import pytesseract

# Path to the image file
image_path = '10.png'
# Load the image using Pillow
img = Image.open(image_path)

# Extract text from image using Tesserac
text = pytesseract.image_to_string(img)

# Print the extracted text
print(text)
