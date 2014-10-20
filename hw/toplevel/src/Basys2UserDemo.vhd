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

  -- Hardwire vga signals to 0 until we have a working vga peripheral.
  VS <= '0';
  HS <= '0';
  OutBlue <= "00";
  OutRed <= "000";
  OutGreen <= "000";
  
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
