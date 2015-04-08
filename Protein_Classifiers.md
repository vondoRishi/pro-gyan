#Pro-Gyan built classifiers.

# Introduction #

We used diverse set of proteins to build protein classifiers. We compared them with the available classifiers on the independent data set.


# Details #

  * PF\_MITO.pgc to classify mitochondrial (MP) proteins of **_Plasmodium_ _Falciparum_** (PF)
> There were several classifier designed for MPs but the most advanced and recent ones are not publicly available to use. Also the specificity of these classifiers was not evaluated on independent test set. The PF\_MITO.pgc was built with N-terminal of training set protein sequences and compared with available different classifier on an independent data set. The test result is given below.
<table width='600' border='1'>
<tr><th>Method</th><th>Sn%</th><th>Sp%</th><th>Acc%</th><th>MCC</th></tr>
<blockquote><tr><td>TargetP</td><td>17.78</td><td>100</td><td>63.9</td><td>0.33</td></tr>
<tr><td>WoLF PSORT</td><td>31.11</td><td>96.52</td><td>67.8</td><td>0.37</td></tr>
<tr><td>MitPred(Pfam+SVM)</td><td>27.78</td><td>100</td><td>68.29</td><td>0.42</td></tr>
<tr><td><a href='http://gecco.org.chemie.uni-frankfurt.de/plasmit/'>PlasMit </a></td><td>58.89</td><td>87.83</td><td>75.12</td><td>0.49</td></tr>
<tr><td><a href='http://www.imtech.res.in/raghava/pfmpred/'>PFMpred </a><b></td></b><td>50</td><td>80</td><td>66.83</td><td>0.32</td></tr>
<tr><td><a href='http://sourceforge.net/projects/progyan/files/Classifiers/PF_MITO.pgc/download'>PF_Mito.pgc</a><code>*</code><br>
[<a href='http://sourceforge.net/projects/progyan/files/Classifiers/Training_Test_sequences/PF_MITO_data.zip/download'>data</a>]</br></td><td>56.67</td><td>93.04</td><td>77.07</td><td>0.54</td></tr>
</table>
<br></blockquote>

<ul><li><a href='http://sourceforge.net/projects/progyan/files/Classifiers/SolubEcoli.pgc/download'>SolubEcoli.pgc</a><b>to classify chaperonin dependent proteins from soluble protein. The model was trained with E.coli proteins. Some of these chaperonin dependent proteins (Class III or C3) are obligatory GroEL substrates. To identify them another classifier</b><a href='http://sourceforge.net/projects/progyan/files/Classifiers/GDP1.pgc/download'>GDP1.pgc</a><b>is available.</li></ul></b>

<ul><li>We also compared Pro-Gyan's ability to build classifiers with different classifiers for different protein classification problems. Identical training and test data from the original classifiers were used and the test result is given below:<br>
<table width='600' border='1'>
<tr><td></td><th>Classifier</th><th>ML Algorithm</th><th>Features</th><th>Accuracy</th><th>MCC</th></tr>
<tr><td>Adhesion</td><td><a href='http://bioinfo.icgeb.res.in/faap/'>FaaPred</a></td><td>SVM</td><td>400</td><td>91.22</td><td>0.68</td></tr>
<tr><td><a href='http://sourceforge.net/projects/progyan/files/Classifiers/FAAP.pgc/download'>FAAP.pgc</a><code>*</code><br>  [<a href='http://sourceforge.net/projects/progyan/files/Classifiers/Training_Test_sequences/FAAP_data.zip/download'>data</a>]</br></td>
</li></ul><blockquote><td>SVM</td><td>90</td><td>96.78</td><td>0.81</td></tr>
<tr><td>Nuclear Receptor</td><td><a href='http://www.plosone.org/article/info%3Adoi%2F10.1371%2Fjournal.pone.0023505'>NR-2L</a></td><td>FK-NN</td><td>881</td><td>98.03</td><td>0.96</td></tr>
<tr><td><a href='http://sourceforge.net/projects/progyan/files/Classifiers/NRP.pgc/download'>NRP.pgc</a><code>*</code><br>  [<a href='http://sourceforge.net/projects/progyan/files/Classifiers/Training_Test_sequences/NR_data.zip/download'>data</a>]</br></td><td>SVM</td><td>400</td><td>99.53</td><td>0.99</td></tr>
</table>