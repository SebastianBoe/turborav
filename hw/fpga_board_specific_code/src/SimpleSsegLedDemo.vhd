----------------------------------------------------------------------------------
-- Company: Digilent RO
-- Engineer: Mircea Dabacan
-- 
-- Create Date:    19:04:55 03/22/2009 
-- Design Name: 
-- Module Name:    SimpleSsegLesDemo - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- This is the source file for the Simple Demo for Basys 2, 
-- provided by the Digilent Reference Component Library.

-- The project demonstrates the behavior of:
--  - seven segment display: all digits count synchronously from 0 to F
--    hexadecimal. All decimal points are turned ON. 
--  - buttons: pressing a button turns OFF the coresponding seven 
--    segment display digit
--  - Switches and LEDs: switches control LEDs state
--
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created 20/03/2009(MirceaD)

-- Additional Comments: 
--
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity SimpleSsegLedDemo is

  Port (ck:  in  std_logic;
        btn: in  std_logic_vector(3 downto 0);
        sw:  in  std_logic_vector(7 downto 0);
        led: out std_logic_vector(7 downto 0);
        seg: out std_logic_vector(6 downto 0);
        dp:  out std_logic;
        an:  out std_logic_vector(3 downto 0)
		  );

end SimpleSsegLedDemo;

architecture Behavioral of SimpleSsegLedDemo is

  signal cntDiv: std_logic_vector(28 downto 0); -- general clock div/cnt
  alias cntDisp: std_logic_vector(3 downto 0) is cntDiv(28 downto 25);
  -- four bits of the main counter

begin

  led <= sw;  -- switches control LEDs

  ckDivider: process(ck)
  begin
    if ck'event and ck='1' then
      cntDiv <= cntDiv + '1';
    end if;
  end process;

  --HEX-to-seven-segment decoder
--   HEX:   in    STD_LOGIC_VECTOR (3 downto 0);
--   LED:   out   STD_LOGIC_VECTOR (6 downto 0);
-- 
-- segment encoinputg
--      0
--     ---  
--  5 |   | 1
--     ---   <- 6
--  4 |   | 2
--     ---
--      3
   
    with cntDisp SELect
   seg<= "1111001" when "0001",   --1
         "0100100" when "0010",   --2
         "0110000" when "0011",   --3
         "0011001" when "0100",   --4
         "0010010" when "0101",   --5
         "0000010" when "0110",   --6
         "1111000" when "0111",   --7
         "0000000" when "1000",   --8
         "0010000" when "1001",   --9
         "0001000" when "1010",   --A
         "0000011" when "1011",   --b
         "1000110" when "1100",   --C
         "0100001" when "1101",   --d
         "0000110" when "1110",   --E
         "0001110" when "1111",   --F
         "1000000" when others;   --0
 
  an <= btn;  -- released buttons turn coresponding digits ON
  dp <= '0';  -- all decimal point ON


end Behavioral;

