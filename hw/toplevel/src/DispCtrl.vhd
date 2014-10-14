library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity DispCtrl is
  Port (
    ck: in std_logic;  -- 50MHz
    
    HS: out std_logic; -- horizontal synchro signal
    VS: out std_logic; -- verical synchro signal
    outRed  : out std_logic_vector(2 downto 0);
    outGreen: out std_logic_vector(2 downto 0);
    outBlue : out std_logic_vector(2 downto 1)
    );
end DispCtrl;

architecture Behavioral of DispCtrl is

-- constants for Synchro module
  constant PAL:integer:=640;	--Pixels/Active Line (pixels)
  constant LAF:integer:=480;	--Lines/Active Frame (lines)
  constant PLD:integer:=800;	--Pixel/Line Divider
  constant LFD:integer:=521;	--Line/Frame Divider
  constant HPW:integer:=96;	--Horizontal synchro Pulse Width (pixels)
  constant HFP:integer:=16;	--Horizontal synchro Front Porch (pixels)
--  constant HBP:integer:=48;	--Horizontal synchro Back Porch (pixels)
  constant VPW:integer:=2;	--Verical synchro Pulse Width (lines)
  constant VFP:integer:=10;	--Verical synchro Front Porch (lines)
--  constant VBP:integer:=29;	--Verical synchro Back Porch (lines)

  
-- signals for VGA Demo
  signal Vcnt: std_logic_vector(9 downto 0);      -- verical counter
  signal intHcnt: integer range 0 to 800-1; --PLD-1 - horizontal counter
  signal intVcnt: integer range 0 to 521-1; -- LFD-1 - verical counter

  signal ck25MHz: std_logic;

-- The Signal that
--      outRed,
--      outGreen,
--      outBlue get their values directly from.
  signal outRGB: std_logic_vector(7 downto 0);
  constant vgaPatternROM: std_logic_vector(639 downto 0):=
    "1111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000";

begin

-- divide 50MHz clock to 25MHz
  div2: process(ck)
  begin
    if ck'event and ck = '1' then
      ck25MHz <= not ck25MHz; 
    end if;
  end process;	 

syncro: process (ck25MHz)
  begin

    if ck25MHz'event and ck25MHz='1' then
      if intHcnt=PLD-1 then
        intHcnt<=0;
        if intVcnt=LFD-1 then
          intVcnt<=0;
        else
          intVcnt<=intVcnt+1;
        end if;
      else
        intHcnt<=intHcnt+1;
      end if;
      
      -- Generates HS - active low
      if intHcnt=PAL-1+HFP then 
        HS<='0';
      elsif intHcnt=PAL-1+HFP+HPW then 
        HS<='1';
      end if;

      -- Generates VS - active low
      if intVcnt=LAF-1+VFP then 
        VS<='0';
      elsif intVcnt=LAF-1+VFP+VPW then
        VS<='1';
      end if;
    end if;
  end process; 

-- mapping itnernal integers to std_logic_vector ports
  Vcnt <= conv_std_logic_vector(intVcnt,10);

-- Colour generation.
-- Since we are only using black and white we can map the RGB variables to a
-- single bus. TODO: Make outRGB one bit or change the output to be a single
-- 8-bit bus.
  outRed   <= outRGB(7 downto 5);
  outGreen <= outRGB(4 downto 2);
  outBlue  <= outRGB(1 downto 0);
  
  mixer: process(ck25MHz,intHcnt, intVcnt) 
  begin
    if intHcnt < PAL and intVcnt < LAF then	-- in the active screen
      outRGB <= (others => vgaPatternROM(intHcnt));
    else
      outRGB <= (others => '0');
    end if;   
  end process;

end Behavioral;
