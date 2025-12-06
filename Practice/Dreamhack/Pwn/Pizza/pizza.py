# Decompiled with PyLingual (https://pylingual.io)
# Internal filename: pizza.py
# Bytecode version: 3.10.0rc2 (3439)
# Source timestamp: 1970-01-01 00:00:00 UTC (0)

global custom_pizza  # inserted
global custom_topping  # inserted
import pickle
import codecs

def welcome(n):
    print('Welcome to our pizzeria, ' + n + '!')
    print('You can choose from a variety of pizzas in our store.')
    print('And if you tell us about any topping, it will be placed on the pizza.')

def menu():
    print()
    print('1 : Input the pizza')
    print('2 : Send toppings info')
    print('3 : Finish the order')

def input_pizza():
    global custom_pizza  # inserted
    print()
    print('Enter the pizza you want.')
    custom_pizza = input()

def input_topping():
    global custom_topping  # inserted
    print()
    print('Enter the toppings you want. Anything is fine!')
    custom_topping = input()
    bom = codecs.BOM_UTF16_LE
    try:
        custom_topping = custom_topping.encode('utf-16')[len(bom):]
    except:
        custom_topping = custom_topping

def check_order(piz, top):
    print()
    print('pizza : ' + piz)
    print('Employee: Shall we look at the topping requests?')
    try:
        print(pickle.loads(top))
    except:
        print(top)
    print('It\'s so hard to make.. Kick that customer out!')
    
custom_pizza = ''
custom_topping = ''
name = input('Please enter your name > ')
welcome(name)
while True:
    menu()
    menu_num = int(input('> '))

    match(menu_num):
        case 1:
            input_pizza()

        case 2:
            input_topping()
            
        case 3:
            break
        case _:
            print('Please enter the correct number')
           
check_order(custom_pizza, custom_topping)