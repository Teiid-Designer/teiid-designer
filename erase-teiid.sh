#!/bin/bash

for f in `find plugins -name MANIFEST.MF`
do
  if [[ "$f" =~ .*\/target\/.* ]]; then
		continue
	fi

  echo $f

#	org.teiid8;bundle-version="[8.0.0,9.0.0)",

	sed -i '/org\.teiid8;bundle-version/d' $f


done
