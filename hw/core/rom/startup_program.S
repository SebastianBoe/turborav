main:
    li x1, 0x20000000
    li x2, 'H'
    li x3, 'e'
    li x4, 'l'
    li x5, 'o'
    li x6, ' '
    li x7, 'W'
    li x8, 'r'
    li x9, 'd'
    li x10, '!'
    sw x2, (x1) # H
    sw x3, (x1) # e
    sw x4, (x1) # l
    sw x4, (x1) # l
    sw x5, (x1) # o
    sw x6, (x1) #
    sw x7, (x1) # W
    sw x5, (x1) # o
    sw x8, (x1) # r
    sw x4, (x1) # l
    sw x9, (x1) # d
    sw x10, (x1) # !
    sw x10, (x1) # !
    j main
