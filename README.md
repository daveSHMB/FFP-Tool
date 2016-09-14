# FFP Tool

Stylometry tool using the Feature Frequency Profiles technique as detailed in:

Sims GE, Jun S-R, Wu GA, Kim S-H. Alignment-free genome comparison with feature frequency profiles (FFP) and optimal resolutions. Proceedings of the National Academy of Sciences of the United States of America. 2009;106(8):2677-2682. doi:10.1073/pnas.0813249106.

The tool enables analysis of a set of literary texts by dividing the contents of each into equally sized 'ngrams' (or character sequences). A frequency count of the occurrence of all ngrams in each text is created, before the difference between each frequency count is computed using the Jensen-Shannon divergence. Finally, using the neighbour-joining method, a phylogenetic tree is constructed, showing the 'ancestry' of each text under survey.

Tree creation and display is enabled using Dendroscope:

Publication: Daniel H. Huson and Celine Scornavacca, Dendroscope 3: An Interactive Tool for Rooted Phylogenetic Trees and Networks, Syst Biol first published online July 10, 2012 doi:10.1093/sysbio/sys062

with neighbour joining functionality provided through the forester library, available at:

https://sites.google.com/site/cmzmasek/home/software/forester

(Tool developed as part of a Master's Degree.)
