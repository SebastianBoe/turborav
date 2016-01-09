import sys
from pint import UnitRegistry

u = UnitRegistry()
for line in sys.stdin:
    print(str((1 / u(line)).to(u.hertz).magnitude))
