import os
import subprocess

# judge parameter. Control this value to make problem.
output_filename='output.txt' # Output will be written on this file
total=1 # Total number of testcases
max_len=2048 # Maximum length of shellcode(in hex string)
time_limit=1 # Maximum execution time(in seconds)

verdicts={'AC':'Accepted','WA':'Wrong Answer','RE':'Runtime Error','TLE':'Time Limit Exceeded','SE':'System error'} # Verdicts
messages={'AC':'Congratulations! You win!','WA':'Output is wrong for certain testcase. Please check the code again.','RE':'Exit code should be 0, or your code made segmentation fault, illegal instruction, etc.','TLE':'The program did not terminated in given time limit. Isn\'t there an infinite loop?','SE':'[!] System Error. Please ask to rootsquare...'} # Messages

def is_hex(s): # Test if given string is valid hex string
    try:
        int(s, 16)
        return True
    except ValueError:
        return False

def judge(shellcode,testcase): # Judge user input
    try:
        with open(output_filename,'wt',encoding='utf-8') as output: # All standard outputs are
            p = subprocess.Popen(["./judge",shellcode], stdout=output, stderr=subprocess.PIPE)
            try:
                return_code=p.wait(time_limit)
                if return_code==0: # Judge the output. Change here to set the judging criteria.
                    try:
                        with open("./output.txt","rt") as output_file:
                            with open(f"./output/{testcase}.txt","rt") as test_file:
                                result=output_file.readline().strip()
                                test=test_file.readline().strip()
                                # print(test, result)
                                return 'AC' if result==test else 'WA'
                    except: # Failed to read a output file
                        return 'SE'
                else: # Return code must be 0
                    return 'RE'
            except subprocess.TimeoutExpired: # Time Limit exceeded
                p.kill()
                return 'TLE'
            except Exception: # System error
                return 'SE'
    except: # Failed to execute judging program
        return 'SE'

print("Input your machine code in hex string.")
shellcode = input("> ")

if len(shellcode)%2 != 0 or is_hex(shellcode)==False:
    print("Please input valid hex string.")
    exit()

if len(shellcode)>max_len:
    print("Input limit exceeded!")
    exit()

result='AC'
for i in range(total): # Test for all testcases
    print('Testcase '+str(i+1)+' : ',flush=True,end='')
    verdict=judge(shellcode,i+1)
    print(verdicts[verdict])
    if os.path.exists('./output.txt'): # Remove the output.txt
        os.remove('./output.txt')
    if verdict!='AC': # Not Accepted
        result=verdict
        break

print(messages[result]) # Print the final result
if result=='AC': # Win to get a flag
    try: # Read flag
        with open('flag.txt','rt') as f:
            flag=f.read().strip()
            print('Flag is '+flag)
    except: # Failed to read flag file
        print('[!] Flag file error. Please ask to rootsquare...')
