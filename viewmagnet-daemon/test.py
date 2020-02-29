import time
print("ABC")

i = 0
while True:
    print("ABC", flush=True)
    i += 1
    if i > 10:
        break
    time.sleep(1)