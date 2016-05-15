/* -*- mode: C -*-
 *
 * Copyright (c) 2007-2011, 2013, 2014 The University of Utah
 * All rights reserved.
 *
 * This file is part of `csmith', a random generator of C programs.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

#ifdef NO_PRINTF
int putchar (int);
#else
extern int printf (const char *, ...);
#endif

// FIXME-- need more versions, and a way to figure out which is needed
#include "custom_stdint_x86.h"

#include "custom_limits.h"

#define STATIC static

#define UNDEFINED(__val) (__val)

#define LOG_INDEX

#define LOG_EXEC

#define FUNC_NAME(x) (safe_##x)

// FIXME
#define assert(x)

#include "safe_math.h"

static inline void platform_main_begin(void)
{
}

static inline void crc32_gentab (void)
{
}

#define _CSMITH_BITFIELD(x) ((x>32)?(x%32):x)

uint64_t crc32_context;

#ifdef TCC
int strcmp (const char *s1, const char *s2)
{
  for(; *s1 == *s2; ++s1, ++s2)
    if(*s1 == 0)
      return 0;
  return *(unsigned char *)s1 < *(unsigned char *)s2 ? -1 : 1;
}
#else
extern int strcmp (const char *, const char *);
#endif

static inline void 
transparent_crc (uint64_t val, char* vname, int flag)
{
  if (flag)
  {
    #ifdef NO_PRINTF
      write(0, vname, strlen(vname));
      write(0, " ", 1);
      for (int i=0; i<16; i++) {
        put_hex (val & 0xf);
        val >>= 4;
      }
      write(0, "\n", 1);
    #else
      printf ("%s %d\n", vname, val);
    #endif
  }
  crc32_context += val;
}

static void 
transparent_crc_bytes (char *ptr, int nbytes, char* vname, int flag)
{
  int i;
  for (i=0; i<nbytes; i++) {
    crc32_context += ptr[i];
  }
  if (flag) {
    printf("...checksum after hashing %s : %lX\n", vname, crc32_context ^ 0xFFFFFFFFUL);
  }
}

#ifdef NO_PRINTF
void my_puts (char *p)
{
  write(0, p, strlen(p));
}

void put_hex (int x)
{
    char * tokens = "0123456789abcdef";
    char * token_ptr = tokens + x;
    write(0, token_ptr, 1);
}
#endif

static inline void
platform_main_end (uint64_t x, int flag)
{
#ifndef NOT_PRINT_CHECKSUM
  if (!flag) {
#ifdef NO_PRINTF
    my_puts ("checksum = ");
    for (uint64_t i=0; i<16; i++) {
      int32_t masked = x & 0xf;
      put_hex (masked);
      x >>= 4;
    }
#else
    printf ("checksum = %llx\n", x);
#endif
  }
#endif

#ifdef RISCV
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("nop");
    asm volatile ("csrwi	tohost, 1");

 infinite_loop:
    goto infinite_loop;
#endif
}
