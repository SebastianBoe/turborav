----------------------------------------------------------------------------------
-- Company: Digilent RO
-- Engineer: Mircea Dabacan
-- 
-- Create Date:    23:51:25 03/22/2009 
-- Design Name:  Basys 2 User Demo
-- Module Name:    Basys2UserDemo - Structural 
-- Project Name: Basys 2
-- Target Devices: Spartan 3E 100 (250)
-- Tool versions: ISE 10.1.03
-- Description: 
--
-- The file contains the structural description of the Basys2 User Demo.
-- It combines the components:
-- - ckMux - to select between mclk and uclk
-- - SimpleSsegLedDemo - to test buttons, switches, LEDs and seven segment display
-- - DispCtrl - to generat VGA signals
-- 
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created
-- Additional Comments: 
--
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity Basys2UserDemo is

   port ( 
          mclk     : in    std_logic; 
          uclk     : in    std_logic; 
	       btn      : in    std_logic_vector (3 downto 0); 
          sw       : in    std_logic_vector (7 downto 0); 
          led      : out   std_logic_vector (7 downto 0); 
          seg      : out   std_logic_vector (6 downto 0); 
          an       : out   std_logic_vector (3 downto 0); 
          dp       : out   std_logic; 
          OutBlue  : out   std_logic_vector (2 downto 1); 
          OutGreen : out   std_logic_vector (2 downto 0); 
          OutRed   : out   std_logic_vector (2 downto 0); 
          HS       : out   std_logic; 
          VS       : out   std_logic
			);

end Basys2UserDemo;

architecture Structural of Basys2UserDemo is

   signal ck50MHz  : std_logic;

   component DispCtrl
      port ( ck       : in    std_logic; 
             HS       : out   std_logic; 
             VS       : out   std_logic; 
             outRed   : out   std_logic_vector (2 downto 0); 
             outGreen : out   std_logic_vector (2 downto 0); 
             outBlue  : out   std_logic_vector (2 downto 1)
				);
   end component;
   
   component SimpleSsegLedDemo
      port ( ck  : in    std_logic; 
             btn : in    std_logic_vector (3 downto 0); 
             sw  : in    std_logic_vector (7 downto 0);
             led : out   std_logic_vector (7 downto 0); 
             seg : out   std_logic_vector (6 downto 0); 
             an  : out   std_logic_vector (3 downto 0);
             dp  : out   std_logic
			  ); 
   end component;
   
   component ckMux
      port ( ck0   : in    std_logic; 
             ck1   : in    std_logic; 
             sel   : in    std_logic; 
             ckOut : out   std_logic
			  );
   end component;
   
begin

   DispCtrlInst : DispCtrl
      port map (
		          ck=>ck50MHz,
                HS=>HS,
                VS=>VS,
                outRed(2 downto 0)=>OutRed(2 downto 0),
                outGreen(2 downto 0)=>OutGreen(2 downto 0),
                outBlue(2 downto 1)=>OutBlue(2 downto 1)
					 );
   
   SimpleSsegLedDemoInst : SimpleSsegLedDemo
      port map (
		          ck=>mclk,
		          btn(3 downto 0)=>btn(3 downto 0),
                sw(7 downto 0)=>sw(7 downto 0),
                led(7 downto 0)=>led(7 downto 0),
                seg(6 downto 0)=>seg(6 downto 0),
                an(3 downto 0)=>an(3 downto 0),
                dp=>dp
				    );
   
   ckMuxInst : ckMux
      port map (
		          ck0=>mclk,
                ck1=>uclk,
                sel=>sw(7),
                ckOut=>ck50MHz
					 );
   
end Structural;

