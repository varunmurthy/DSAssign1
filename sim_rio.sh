#!/bin/bash

rm -rf storage
rm -f *.log
rm -f *.replay
#./execute.pl -s -n RIOTester -f 0 -c scripts/RIOTest

./execute.pl -s -n TwoGenerals -f 0 -c scripts/TwoGenerals
