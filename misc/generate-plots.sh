#!/bin/bash
gnuplot -e "var='coarse'" speedup_plot_script
gnuplot -e "var='fine'" speedup_plot_script
