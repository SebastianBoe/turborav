#define GPIO (volatile uint32_t *)0x20000000

/*
   Returns the frequency that the CPU is currently running at in Hz.
*/
uint32_t get_cpu_freq_hz(void);

/*
  Returns a best-effort estimation of the number of CPU clock cycles
  spent during one iteration of a loop like for(int i = 0; i < x; i++)
  asm("nop");
 */
uint32_t get_loop_with_nop_cycle_cost(void);
