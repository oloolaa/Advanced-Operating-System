hosts=(dc01 dc02 dc03 dc04 dc05 dc06 dc07 dc08 dc09 dc10 dc11 dc12 dc13 dc14 dc15 dc16 dc17 dc18 dc19 dc20 dc21 dc22 dc23 dc24 dc25 dc26 dc27 dc28 dc29 dc30)

for host in ${hosts[@]}
do
 ssh $(whoami)@$host killall -u $(whoami)
done
