class TooMuchDelimitersError(Exception):
    """
    Exception raised due to number of delimeters.
    """
    def __init__(self, message="Too much delimiters were found: skip...\n"):
        self.message = message
        super().__init__(self.message)


class WrongSymbolsError(Exception):
    """
    Exception raised due to wrong symbols in input.
    """
    def __init__(self, message="Wrong symbols were found: skip...\n"):
        self.message = message
        super().__init__(self.message)


def process_input(input_string: str) -> (int | float):
    input_string = input_string.strip()
    if "," in input_string or "." in input_string:
        
        if input_string.count(',') + input_string.count('.') > 1:
            raise TooMuchDelimitersError()
        
        input_string = input_string.split(',') if "," in input_string else input_string.split('.')
        sign = -1 if len(input_string[0]) != 0 and input_string[0][0] == "-" else 1
        if len(input_string[0]) == 0:
            integer = 0
        else:
            try:
                integer = int(input_string[0])
            except ValueError:
                raise WrongSymbolsError()
        
        if len(input_string[1]) == 0:
            mantissa = 0
        else:
            try:
                mantissa = int(input_string[1])
            except ValueError:
                raise WrongSymbolsError()
            
        output = (integer + (sign * mantissa/10**len(input_string[1])))
    else:
        try:
            output = int(input_string)
        except ValueError:
            raise WrongSymbolsError()
    
    return output


def calc_remainders(num: (int | float)) -> list:
    if num == 0: return [0]
    
    if type(num) is float:
        integer = int(num)
        mantissa = num - int(num)
        
        remainders_int = []
        while integer != 0:
            remainder = int(integer % 7)
            integer //= 7
            if remainder > 3:
                remainder -= 7
                integer += 1
            elif remainder < -3:
                remainder += 7
                integer -= 1
            remainders_int.append(remainder)
        
        remainders_int.reverse()
        
        remainders_mant = []
        for _ in range(10):
            if mantissa == 0:
                break
            mantissa *= 7
            rem = int(mantissa)
            
            if rem > 3:
                rem -= 7
            elif rem < -3:
                rem += 7
            
            mantissa -= int(mantissa)
            
            remainders_mant.append(rem)
        remainders = [remainders_int,  remainders_mant]
    
    else:
        remainders = []
        while num != 0:
            remainder = int(num % 7)
            num //= 7
            if remainder > 3:
                remainder -= 7
                num += 1
            elif remainder < -3:
                remainder += 7
                num -= 1
            remainders.append(remainder)
        remainders.reverse()
        
    return remainders

def build_answer(digits: list) -> str:
    DIGITS_MAP = {
        -3: "{^3}",
        -2: "{^2}",
        -1: "{^1}",
        0: "0",
        1: "1",
        2: "2",
        3: "3",
    }

    if type(digits[0]) is list:
        output = ""
        part = ""
        if len(digits[0]) == 0:
            part += "0"
        for i in digits[0]:
            part += DIGITS_MAP[i]
        
        output += part + "."
        part = ""
        for i in digits[1]:
            part += DIGITS_MAP[i]
                
        output += part
        
    else:
        output = ""
        for i in digits:
            output += DIGITS_MAP[i]
                
    return output


def main():    
    while True:
        try:
            s = input("Your input here: ")
        except EOFError: break
        
        try:
            num = process_input(s)
        except WrongSymbolsError as ex:
            print(ex)
            continue
        except TooMuchDelimitersError as ex:
            print(ex)
            continue
        
        digits = calc_remainders(num)
        print(f"The result is {build_answer(digits)}\n")

if __name__ == "__main__":
    main()
    