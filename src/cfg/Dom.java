package cfg;

import java.util.ArrayList;
import java.util.HashSet;

public class Dom {
    public static void storeDominators(CFG cfg) {
        for (int j = 0; j < cfg.getNumFuncs(); j++) {
            ArrayList<BasicBlock> blocks = new ArrayList<>();
            HashSet<String> blockNames = cfg.getFuncBlocks(j);

            for (BasicBlock b : cfg.getBlocks()) {
                if (blockNames.contains(b.getName())) {
                    blocks.add(b);
                }
            }

            int n = blocks.size();
            blocks.get(0).addDominator(blocks.get(0));
            for (int i = 1; i < n; i++) {
                blocks.get(i).setDominators(new HashSet<>(blocks));
            }

            boolean changed = true;
            while (changed) {
                changed = false;
                for (int i = 1; i < n; i++) {
                    BasicBlock b = blocks.get(i);
                    HashSet<BasicBlock> temp = new HashSet<>();
                    for (BasicBlock p : b.getParents()) {
                        if (temp.isEmpty()) {
                            temp = new HashSet<>(p.getDominators());
                        } else {
                            temp.retainAll(new HashSet<>(p.getDominators()));
                        }
                    }
                    temp.add(b);
                    if (!temp.equals(b.getDominators())) {
                        b.setDominators(temp);
                        changed = true;
                    }
                }
            }
        }

        for (BasicBlock b : cfg.getBlocks()) {
            for (BasicBlock dom : b.getDominators()) {
                if (dom == b) {
                    continue;
                }
                if (b.getiDom() == null || dom.getNum() > b.getiDom().getNum()) {
                    b.setiDom(dom);
                }
            }
        }

        for (BasicBlock b : cfg.getBlocks()) {
            if (b.getParents().size() > 1) {
                for (BasicBlock p : b.getParents()) {
                    BasicBlock runner = p;
                    while (runner != b.getiDom()) {
                        runner.getDF().add(b);
                        runner.setDF(runner.getDF());
                        runner = runner.getiDom();
                    }
                }
            }
        }
    }
}
