#!/usr/bin/env python3
import glob
import os
import random
import sys
import threading
import time
from scapy.all import IP, UDP, Raw, send, conf, raw

def gen_random_fragment_sizes(total_len: int, min_sz: int, max_sz: int, mtu: int):
    sizes = []
    remaining = total_len
    max_payload_per_frag = max(8, mtu - 20)

    first = True
    while remaining > 0:
        if remaining <= max_payload_per_frag:
            sizes.append(remaining)
            break

        upper = min(max_sz, max_payload_per_frag, remaining - 1)
        lower = min_sz

        if upper < 8:
            sz = (max(8, min(remaining - 1, max_payload_per_frag)) // 8) * 8
        else:
            if lower > upper:
                lower = max(8, min(upper, remaining - 1))
            sz = random.randint(lower, upper)
            sz = max(8, (sz // 8) * 8)

        if first and sz < 8:
            sz = 8
        first = False

        if sz > max_payload_per_frag:
            sz = (max_payload_per_frag // 8) * 8
            sz = max(8, sz)

        sizes.append(sz)
        remaining -= sz

    return sizes

def send_file(path):
    data  = open(path, "rb").read()
    data += b"\x00"*0x100
    assert len(data) <= 1500

    # I should've removed the following line ;(
    # os.unlink(path)

    base = IP(src=src_ip, dst=dst_ip, ttl=64, tos=0) / \
        UDP(sport=31337, dport=1337) / \
        Raw(data)
    ip_payload = raw(base)[20:]

    sizes = gen_random_fragment_sizes(len(ip_payload), 0x10, 0x100, 0x100)

    off = 0
    ip_id = random.randint(0, 0xFFFF)
    for i, sz in enumerate(sizes):
        chunk = ip_payload[off:off+sz]
        mf = 1 if i < len(sizes)-1 else 0
        if mf == 0:
            print(".", end="", flush=True)
            time.sleep(1000)
        frag = IP(src=src_ip, dst=dst_ip, ttl=64, tos=0,
                  id=ip_id, flags=mf, frag=off//8, proto=17) / Raw(chunk)
        send(frag, iface=iface, verbose=0)
        off += sz
        time.sleep(random.random() * 2)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(f"Usage: {sys.argv[0]} <src> <dst>")
    src_ip, dst_ip = sys.argv[1], sys.argv[2]

    threads = []
    for path in glob.glob("files/*"):
        th = threading.Thread(target=send_file, args=(path,))
        th.start()
        threads.append(th)

    for th in threads:
        th.join()
