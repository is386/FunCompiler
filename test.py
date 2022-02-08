class Block:
    def __init__(self, val) -> None:
        self.val = val
        self.parents = set()
        self.dom = set()

    def add_parent(self, block):
        self.parents.add(block)

    def __str__(self):
        s = str(self.val) + ": "
        for p in self.parents:
            s += str(p.val) + ", "
        return s


blocks = []
for i in range(7):
    blocks.append(Block(i))

blocks[1].add_parent(blocks[0])
blocks[1].add_parent(blocks[6])
blocks[2].add_parent(blocks[1])
blocks[3].add_parent(blocks[2])
blocks[4].add_parent(blocks[2])
blocks[5].add_parent(blocks[3])
blocks[6].add_parent(blocks[5])

n = len(blocks)
blocks[0].dom = set([blocks[0]])
for i in range(1, n):
    blocks[i].dom = set(blocks)

changed = True
while changed:
    changed = False
    for i in range(1, n):
        temp = set()
        for p in blocks[i].parents:
            if len(temp) == 0:
                temp = p.dom
            else:
                temp = temp.intersection(p.dom)
        temp = temp.union(set([blocks[i]]))
        if temp != blocks[i].dom:
            blocks[i].dom = temp
            changed = True

for block in blocks:
    s = str(block.val) + ": "
    for dom in block.dom:
        s += str(dom.val) + " "
    print(s)
