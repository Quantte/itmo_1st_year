# Author = Gerasiuto Fadey Aleksandrovich
# Group = P3119
# Date = 21.10.2025

import re

tests = [
    "rtrt45rt655eAAAAA$February",
    "KL4D0VK4_P4VLU*xx!july23>1337",
    "AAaa11!july",
    "P4ssw0rd!july23>2",
    "QWErty1!dec"
]

for test in tests:
    if re.search(r"\S{5,}", test) is None:
        print("Your password must include at least 5 characters")
        continue
    
    if re.search(r"\d", test) is None:
        print("Your password must include a number")
        continue

    if re.search(r"[A-Z]", test) is None: 
        print("Your password must include an uppercase letter")
        continue

    if re.search(r"(\W|_)", test) is None: 
        print("Your password must include a special character")
        continue

    if re.search(r"(january|february|march|april|may|june|july|august|september|october|november|december)", test, re.IGNORECASE) is None: 
        print("Your password must include a month of the year")
        continue

    if sum(map(int, re.findall(r"\d", test))) != 25:
        print("The digits in your passport must add up to 25")
        continue

    print("Your password is strong enough")

