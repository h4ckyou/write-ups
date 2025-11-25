; Disassembly of rom.bin
; Disassembled Mon Dec  9 16:02:30 2024
; Using Stella 7.0
;
; ROM properties name : rom
; ROM properties MD5  : 82795c1b840be1814c2f447bbc1a7fd4
; Bankswitch type     : 4K* (4K) 
;
; Legend: *  = CODE not yet run (tentative code)
;         D  = DATA directive (referenced in some way)
;         G  = GFX directive, shown as '#' (stored in player, missile, ball)
;         P  = PGFX directive, shown as '*' (stored in playfield)
;         C  = COL directive, shown as color constants (stored in player color)
;         CP = PCOL directive, shown as color constants (stored in playfield color)
;         CB = BCOL directive, shown as color constants (stored in background color)
;         A  = AUD directive (stored in audio registers)
;         i  = indexed accessed only
;         c  = used by code executed in RAM
;         s  = used by stack
;         !  = page crossed, 1 cycle penalty

    processor 6502


;-----------------------------------------------------------
;      Color constants
;-----------------------------------------------------------

BLACK            = $00
YELLOW           = $10
BROWN            = $20
ORANGE           = $30
RED              = $40
MAUVE            = $50
VIOLET           = $60
PURPLE           = $70
BLUE             = $80
BLUE_CYAN        = $90
CYAN             = $a0
CYAN_GREEN       = $b0
GREEN            = $c0
GREEN_YELLOW     = $d0
GREEN_BEIGE      = $e0
BEIGE            = $f0


;-----------------------------------------------------------
;      TIA and IO constants accessed
;-----------------------------------------------------------

INPT4           = $0c  ; (R)
INPT5           = $0d  ; (R)

VSYNC           = $00  ; (W)
VBLANK          = $01  ; (W)
WSYNC           = $02  ; (W)
COLUP0          = $06  ; (W)
GRP0            = $1b  ; (W)



;-----------------------------------------------------------
;      RIOT RAM (zero-page) labels
;-----------------------------------------------------------

ram_80          = $80
ram_81          = $81
ram_82          = $82
ram_83          = $83
ram_84          = $84
ram_85          = $85


;-----------------------------------------------------------
;      User Defined Labels
;-----------------------------------------------------------

Start           = $f156


;***********************************************************
;      Bank 0
;***********************************************************

    SEG     CODE
    ORG     $f000

    .byte   %00000000 ; |        |            $f000 (G)
    .byte   %01000010 ; | #    # |            $f001 (G)
    .byte   %01000010 ; | #    # |            $f002 (G)
    .byte   %01000010 ; | #    # |            $f003 (G)
    .byte   %01111110 ; | ###### |            $f004 (G)
    .byte   %01111110 ; | ###### |            $f005 (G)
    .byte   %01000010 ; | #    # |            $f006 (G)
    .byte   %01000010 ; | #    # |            $f007 (G)
    .byte   %01000010 ; | #    # |            $f008 (G)
    
    .byte   $00,$18,$18,$18,$18,$18,$18,$18 ; $f009 (*)
    .byte   $ff,$00,$e0,$90,$90,$90,$e0,$90 ; $f011 (*)
    .byte   $90,$e0,$00,$30,$40,$40,$c0,$c0 ; $f019 (*)
    .byte   $40,$40,$30,$00,$fe,$82,$82,$82 ; $f021 (*)
    .byte   $fe,$80,$80,$fe,$00,$3e,$40,$80 ; $f029 (*)
    .byte   $bc,$c2,$82,$44,$38,$00,$01,$01 ; $f031 (*)
    .byte   $01,$7f,$41,$41,$41,$7f,$00,$fc ; $f039 (*)
    .byte   $84,$84,$84,$fc,$80,$80,$fc,$00 ; $f041 (*)
    .byte   $3f,$21,$21,$21,$3f,$01,$01,$01 ; $f049 (*)
    .byte   $00,$7e,$42,$42,$7e,$7e,$42,$42 ; $f051 (*)
    .byte   $7e,$00,$3e,$40,$80,$bc,$c2,$82 ; $f059 (*)
    .byte   $44,$38,$00,$7e,$42,$62,$52,$4a ; $f061 (*)
    .byte   $46,$42,$7e,$00,$04,$04,$04,$7c ; $f069 (*)
    .byte   $44,$44,$44,$40,$00,$04,$04,$04 ; $f071 (*)
    .byte   $7c,$44,$44,$44,$40,$00,$7e,$42 ; $f079 (*)
    .byte   $62,$52,$4a,$46,$42,$7e,$00,$04 ; $f081 (*)
    .byte   $04,$04,$7c,$44,$44,$44,$40,$00 ; $f089 (*)
    .byte   $1a,$26,$22,$12,$0e,$02,$22,$1c ; $f091 (*)
    .byte   $00,$fc,$84,$84,$84,$fc,$80,$80 ; $f099 (*)
    .byte   $fc,$00,$70,$48,$48,$48,$70,$40 ; $f0a1 (*)
    .byte   $40,$40,$00,$3c,$04,$04,$04,$3c ; $f0a9 (*)
    .byte   $20,$20,$3c,$00,$02,$02,$02,$02 ; $f0b1 (*)
    .byte   $02,$0a,$06,$02,$00,$40,$40,$40 ; $f0b9 (*)
    .byte   $e0,$40,$50,$50,$20,$00,$7e,$42 ; $f0c1 (*)
    .byte   $62,$52,$4a,$46,$42,$7e,$00,$00 ; $f0c9 (*)
    .byte   $18,$20,$20,$20,$18,$00,$00,$00 ; $f0d1 (*)
    .byte   $3f,$21,$21,$21,$3f,$01,$01,$01 ; $f0d9 (*)
    .byte   $00,$00,$18,$20,$20,$20,$18,$00 ; $f0e1 (*)
    .byte   $00,$00,$3e,$40,$80,$bc,$c2,$82 ; $f0e9 (*)
    .byte   $44,$38,$00,$02,$02,$02,$02,$02 ; $f0f1 (*)
    .byte   $0a,$06,$02,$00,$10,$10,$10,$08 ; $f0f9 (*)
    .byte   $04,$02,$02,$1e,$00,$02,$02,$02 ; $f101 (*)
    .byte   $02,$02,$0a,$06,$02,$00,$7e,$42 ; $f109 (*)
    .byte   $42,$7e,$7e,$42,$42,$7e,$00,$1a ; $f111 (*)
    .byte   $26,$22,$12,$0e,$02,$22,$1c,$00 ; $f119 (*)
    .byte   $7e,$42,$62,$52,$4a,$46,$42,$7e ; $f121 (*)
    .byte   $00,$3e,$40,$80,$bc,$c2,$82,$44 ; $f129 (*)
    .byte   $38,$00,$3e,$40,$80,$bc,$c2,$82 ; $f131 (*)
    .byte   $44,$38,$00,$70,$48,$48,$48,$70 ; $f139 (*)
    .byte   $40,$40,$40,$00,$0c,$02,$02,$03 ; $f141 (*)
    .byte   $03,$02,$02,$0c,$00,$3c,$42,$bd ; $f149 (*)
    .byte   $e7,$ff,$db,$7e,$3c             ; $f151 (*)
    
Start
    sei                             ;2        
    cld                             ;2        
    ldx     #$00                    ;2        
    txa                             ;2        
    tay                             ;2   =  10
Lf15c
    dex                             ;2        
    txs                             ;2        
    pha                             ;3        
    bne     Lf15c                   ;2/3      
    lda     #$ff                    ;2        
    sta     ram_85                  ;3        
    lda     #$05                    ;2        
    sta     ram_81                  ;3        
    lda     #$f0                    ;2        
    sta     ram_83                  ;3   =  24
Lf16d
    lda     #$02                    ;2        
    sta     VBLANK                  ;3        
    sta     VSYNC                   ;3        
    sta     WSYNC                   ;3   =  11
;---------------------------------------
    sta     WSYNC                   ;3   =   3
;---------------------------------------
    sta     WSYNC                   ;3   =   3
;---------------------------------------
    lda     #$00                    ;2        
    sta     VSYNC                   ;3        
    ldx     #$25                    ;2   =   7
Lf17f
    sta     WSYNC                   ;3   =   3
;---------------------------------------
    dex                             ;2        
    bne     Lf17f                   ;2/3      
    stx     VBLANK                  ;3        
    ldx     #$c0                    ;2   =   9
Lf188
    txa                             ;2        
    sec                             ;2        
    sbc     ram_81                  ;3        
    cmp     #$09                    ;2        
    bcc     Lf196                   ;2/3      
    lda     #$00                    ;2        
    cmp     ram_80                  ;3        
    bne     Lf196                   ;2/3 =  18
Lf196
    tay                             ;2        
    lda     (ram_82),y              ;5        
    sta     WSYNC                   ;3   =  10
;---------------------------------------
    sta     GRP0                    ;3        
    lda     #BLACK|$0               ;2        
    sta     COLUP0                  ;3        
    dex                             ;2        
    bne     Lf188                   ;2/3      
    lda     #$02                    ;2        
    sta     VBLANK                  ;3        
    ldx     #$1e                    ;2   =  19
Lf1aa
    sta     WSYNC                   ;3   =   3
;---------------------------------------
    dex                             ;2        
    bne     Lf1aa                   ;2/3      
    inc     ram_80                  ;5        
    lda     ram_80                  ;3        
    cmp     ram_85                  ;3        
    bne     Lf1e9                   ;2/3      
    lda     #$00                    ;2         *
    sta     ram_80                  ;3         *
    bit     INPT4                   ;3         *
    bmi     Lf1e9                   ;2/3       *
    bit     INPT5                   ;3         *
    bmi     Lf1e9                   ;2/3       *
    lda     ram_84                  ;3         *
    cmp     #$25                    ;2         *
    bcs     Lf1e9                   ;2/3       *
    inc     ram_84                  ;5         *
    lda     ram_84                  ;3         *
    cmp     #$1d                    ;2         *
    bcc     Lf1e2                   ;2/3       *
    lda     #$f1                    ;2         *
    sta     ram_83                  ;3         *
    lda     #$84                    ;2         *
    cmp     #$1d                    ;2         *
    bne     Lf1e2                   ;2/3       *
    lda     #$05                    ;2         *
    sta     ram_82                  ;3         *
    jmp     Lf1e9                   ;3   =  70 *
    
Lf1e2
    lda     ram_82                  ;3         *
    clc                             ;2         *
    adc     #$09                    ;2         *
    sta     ram_82                  ;3   =  10 *
Lf1e9
    jmp     Lf16d                   ;3   =   3
    
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f1ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f1f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f1fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f204 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f20c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f214 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f21c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f224 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f22c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f234 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f23c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f244 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f24c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f254 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f25c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f264 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f26c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f274 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f27c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f284 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f28c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f294 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f29c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f2fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f304 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f30c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f314 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f31c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f324 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f32c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f334 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f33c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f344 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f34c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f354 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f35c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f364 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f36c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f374 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f37c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f384 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f38c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f394 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f39c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f3fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f404 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f40c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f414 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f41c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f424 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f42c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f434 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f43c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f444 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f44c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f454 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f45c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f464 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f46c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f474 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f47c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f484 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f48c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f494 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f49c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f4fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f504 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f50c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f514 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f51c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f524 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f52c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f534 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f53c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f544 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f54c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f554 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f55c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f564 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f56c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f574 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f57c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f584 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f58c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f594 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f59c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f5fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f604 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f60c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f614 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f61c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f624 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f62c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f634 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f63c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f644 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f64c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f654 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f65c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f664 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f66c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f674 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f67c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f684 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f68c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f694 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f69c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f6fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f704 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f70c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f714 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f71c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f724 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f72c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f734 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f73c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f744 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f74c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f754 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f75c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f764 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f76c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f774 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f77c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f784 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f78c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f794 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f79c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f7fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f804 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f80c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f814 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f81c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f824 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f82c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f834 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f83c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f844 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f84c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f854 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f85c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f864 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f86c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f874 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f87c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f884 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f88c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f894 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f89c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f8fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f904 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f90c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f914 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f91c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f924 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f92c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f934 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f93c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f944 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f94c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f954 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f95c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f964 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f96c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f974 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f97c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f984 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f98c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f994 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f99c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9a4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9ac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9b4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9bc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9c4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9cc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9d4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9dc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9e4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9ec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9f4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $f9fc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa04 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa0c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa14 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa1c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa24 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa2c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa34 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa3c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa44 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa4c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa54 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa5c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa64 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa6c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa74 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa7c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa84 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa8c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa94 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fa9c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $faa4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $faac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fab4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fabc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fac4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $facc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fad4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fadc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fae4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $faec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $faf4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fafc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb04 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb0c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb14 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb1c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb24 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb2c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb34 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb3c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb44 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb4c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb54 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb5c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb64 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb6c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb74 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb7c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb84 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb8c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb94 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fb9c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fba4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbb4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbbc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbc4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbcc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbd4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbdc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbe4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbf4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fbfc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc04 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc0c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc14 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc1c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc24 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc2c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc34 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc3c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc44 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc4c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc54 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc5c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc64 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc6c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc74 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc7c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc84 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc8c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc94 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fc9c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fca4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcb4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcbc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcc4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fccc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcd4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcdc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fce4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcf4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fcfc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd04 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd0c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd14 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd1c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd24 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd2c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd34 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd3c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd44 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd4c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd54 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd5c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd64 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd6c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd74 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd7c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd84 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd8c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd94 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fd9c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fda4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdb4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdbc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdc4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdcc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdd4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fddc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fde4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdf4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fdfc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe04 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe0c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe14 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe1c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe24 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe2c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe34 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe3c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe44 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe4c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe54 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe5c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe64 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe6c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe74 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe7c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe84 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe8c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe94 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fe9c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fea4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $feac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $feb4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $febc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fec4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fecc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fed4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fedc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fee4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $feec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fef4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fefc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff04 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff0c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff14 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff1c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff24 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff2c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff34 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff3c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff44 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff4c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff54 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff5c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff64 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff6c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff74 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff7c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff84 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff8c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff94 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ff9c (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffa4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffac (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffb4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffbc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffc4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffcc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffd4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffdc (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffe4 (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $ffec (*)
    .byte   $ff,$ff,$ff,$ff,$ff,$ff,$ff,$ff ; $fff4 (*)
    .byte   $56,$f1                         ; $fffc (D)
    .byte   $56                             ; $fffe (*)
    .byte   $f1                             ; $ffff (*)
