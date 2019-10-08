The simulation model consists of components connected by wires.

Some components consist of other components and wires. The components contained in such a component are called the subcomponents of this component.

Some wires carry one bit, and some carry multiple bits.

Each bit carried by a wire is one of 1, 0, U, Z, or X. These values are IEEE 1164-compliant. (We don't use H, L, or W.)  
See https://en.wikipedia.org/wiki/IEEE_1164 for an explanation of these values.  
Note: We also use X to denote illegal states. For example, when two components try to put different values on the D bus, the conflicting bits on the D bus will become X.

The simulation model can be viewed in the Simulation view:  
Each rectangle represents a component. Zooming in a component reveals its inner structure (if it has one).  
The colored lines connecting these rectangles represent wires.  
(Multiple-bit wires are displayed thicker than the single-bit wires, and always are black.  
For single-bit wires, the color represents their current value. These colors can be configured in the Eclipse preferences. The default values are:

| Bit | Color  |
|:---:| ------ |
| 1   | green  |
| 0   | grey   |
| U   | cyan   |
| Z   | yellow |
| X   | red    |