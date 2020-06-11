from random import randint

def fib(dic, n):
    if n <= 2: return 1
    if n not in dic:
        dic[n] = fib(dic, n-1) + fib(dic, n-2)
    return dic[n]

def print_fib():
    n = randint(1, 100)
    dic = {}
    print(fib(dic, n), end = " ")
    print_fib()

print_fib()
