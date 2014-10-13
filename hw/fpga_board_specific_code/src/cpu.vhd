library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity CPU is
  Port(
    i_ck: in std_logic;
    i_instruction: in std_logic_vector(15 downto 0);
    i_data: in std_logic_vector(15 downto 0););
    
