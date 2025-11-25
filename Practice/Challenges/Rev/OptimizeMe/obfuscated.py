# good luck!

class Obfuscated:
    def __init__ (self):
      self.position = 0


    def get_character(self, idx ,key ):
      array = [115 ,104 ,100 ,120 ,98 ,89 ,233 ,2 ,189 ,113 ,15 ,64 ,78 ,80 ,16 ,75 ,108 ,184 ,217 ,90 ,148 ,78 ,184 ,45 ,57 ,62 ,144 ,36 ,154 ,224 ,143 ,239 ,137 ,57 ,121 ,235 ,85 ,242 ,106 ,14 ,208 ,36 ,192 ,244 ]
      return array[idx] ^ (key & 0xff )
    

    def get_key(self, position):
      if position <= 1 :
        return position
      
      return self .get_key(position - 1) + self.get_key(position - 2)
  
    
    def give_flag(self):
      key = self.get_key(self.position * 2)
      character = self.get_character(self.position ,key )
      self.position +=1
      return chr(character)
    
def main():
  obj = Obfuscated()

  while True :
    user_input = input('Hello! How can I help you? ')

    if user_input == 'Please give me another character of the flag':
     print ('Ok, here you go: '+ obj.give_flag())
    elif user_input == 'Give me another character of the flag':
      print ('No, why should I?')
    else :
     print('Too fast! Can you say that again?')

if __name__=='__main__':
 main()
