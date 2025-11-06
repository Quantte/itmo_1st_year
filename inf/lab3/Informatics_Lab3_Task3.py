# Author = Gerasiuto Fadey Aleksandrovich
# Group = P3119
# Date = 21.10.2025

import re

tests = [
    "rtrt45rt655eAAAAA$FebRuary",
    "KL4D0VK4_P4VLU*xx!july23>1337",
    "AAaa11!july",
    "P4ssw0rd!july23>2",
    "QWErty1!dec"
]

for test in tests:
    conditions = [
        re.search(r"\S{5,}", test), 
        re.search(r"\d", test),
        re.search(r"[A-Z]", test),
        re.search(r"(\W|_)", test), 
        re.search(r"(january|february|march|april|may|june|july|august|september|october|november|december)", test, re.IGNORECASE), 
        sum(map(int, re.findall(r"\d", test))) == 25
    ]

    if all(conditions):
        print("Your password is strong enough")
    else:
        print("Your password doesn't suit")
