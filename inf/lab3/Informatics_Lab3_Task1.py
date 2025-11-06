# Author = Gerasiuto Fadey Aleksandrovish
# Group = P3119
# Date = 21.10.2025

import re


tests = [
    "А ты знал, что ВТ – лучшая кафедра d в ИТМО?А ты знал, что ВТ – лучшая кафедра в ИТМО?",
    "ВТ ИТМО",
    "ВТ – лучшая кафедра в университете ИТМО.",
    "Про ВТ я не слышал ничего в ИТМО.",
    "Я учусь на ВТ. Однако в ИТМО есть много разных кафедр."
]


for test in tests:
    pattern = r'\bВТ\b(?:\W+\w+){0,4}\W+\bИТМО\b'
    results = re.finditer(pattern, test) 
    for res in results:
        print(res.group())
    print('-------------------------------')

