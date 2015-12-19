// See LICENSE for license details.

#ifndef _HWACHA_XCPT_H
#define _HWACHA_XCPT_H

#define HWACHA_CAUSE_ILLEGAL_CFG 0 // AUX: 0=illegal nxpr, 1=illegal nfpr
#define HWACHA_CAUSE_ILLEGAL_INSTRUCTION 1 // AUX: instruction
#define HWACHA_CAUSE_PRIVILEGED_INSTRUCTION 2 // AUX: instruction
#define HWACHA_CAUSE_TVEC_ILLEGAL_REGID 3 // AUX: instruction
#define HWACHA_CAUSE_VF_MISALIGNED_FETCH 4 // AUX: pc
#define HWACHA_CAUSE_VF_FAULT_FETCH 5 // AUX: pc
#define HWACHA_CAUSE_VF_ILLEGAL_INSTRUCTION 6 // AUX: pc
#define HWACHA_CAUSE_VF_ILLEGAL_REGID 7 // AUX: pc
#define HWACHA_CAUSE_MISALIGNED_LOAD 8 // AUX: badvaddr
#define HWACHA_CAUSE_MISALIGNED_STORE 9 // AUX: badvaddr
#define HWACHA_CAUSE_FAULT_LOAD 10 // AUX: badvaddr
#define HWACHA_CAUSE_FAULT_STORE 11 // AUX: badvaddr

#endif
