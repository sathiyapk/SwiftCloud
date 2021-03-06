package swift.deployment

class PlanetLab_3Xx {

    static int maxPerRegion = 32

    static def PlanetLab_EU_RAW = [
        "planetlab2.extern.kuleuven.be",
        "orval.infonet.fundp.ac.be",
        "planck227ple.test.ibbt.be",
        "planetlab2.montefiore.ulg.ac.be",
        "plewifi.ipv6.lip6.fr",
        "ple2.ipv6.lip6.fr",
        "ple3.ipv6.lip6.fr",
        "host3-plb.loria.fr",
        "ple4.ipv6.lip6.fr",
        "ple6.ipv6.lip6.fr",
        "planetlab1.montefiore.ulg.ac.be",
        "peeramide.irisa.fr",
        "host4-plb.loria.fr",
        "inriarennes1.irisa.fr",
        "lsirextpc01.epfl.ch",
        "planetlab2.thlab.net",
        "planetlab1.thlab.net",
        "iraplab2.iralab.uni-karlsruhe.de",
        "peeramidion.irisa.fr",
        "planetlab1.unineuchatel.ch",
        "planet1.zib.de",
        "inriarennes2.irisa.fr",
        "planet02.hhi.fraunhofer.de",
        "onelab-2.fhi-fokus.de",
        "planetlab4.inf.ethz.ch",
        "planetlab-1.cs.unibas.ch",
        "pl1.bell-labs.fr",
        "dfn-ple1.x-win.dfn.de",
        //        "onelab-1.fhi-fokus.de",
        "planetlab1.inf.ethz.ch",
        "host1.planetlab.informatik.tu-darmstadt.de",
        "planetlab01.ethz.ch",
        "host2.planetlab.informatik.tu-darmstadt.de",
        "iraplab1.iralab.uni-karlsruhe.de",
        "planetlab3.inf.ethz.ch",
        "planetlab2.wiwi.hu-berlin.de",
        "planetlab1.informatik.uni-goettingen.de",
        "merkur.planetlab.haw-hamburg.de",
        "planetlab2lannion.elibel.tm.fr",
        "planetlab1lannion.elibel.tm.fr",
        "planetlab2.inf.ethz.ch",
        "planetlab-2.cs.unibas.ch",
        "planetlab1.diku.dk",
        "planetlab2.diku.dk",
        "planetlab2.csg.uzh.ch",
        "ple02.fc.univie.ac.at",
        "planetlab2.informatik.uni-goettingen.de",
        "ple01.fc.univie.ac.at",
        "planetlab02.tkn.tu-berlin.de",
        "planetlab1.informatik.uni-leipzig.de",
        "planetlab-1.fokus.fraunhofer.de",
        "planetlab-2.man.poznan.pl",
        "planetlab1.uc3m.es",
        "planetlab1.exp-math.uni-essen.de",
        "planetlab1.csg.uzh.ch",
        "planetlab2.exp-math.uni-essen.de",
        "planetlab01.mpi-sws.mpg.de",
        "openlab02.pl.sophia.inria.fr",
        "planetlab-2.fokus.fraunhofer.de",
        "mars.planetlab.haw-hamburg.de",
        "planetlab2.eecs.jacobs-university.de",
        "planet01.hhi.fraunhofer.de",
        "planetlab2.dit.upm.es",
        "planetlab1.u-strasbg.fr",
        "node2pl.planet-lab.telecom-lille1.eu",
        "planetlab02.mpi-sws.mpg.de",
        "planetlab2.cesnet.cz",
        "planet-plc-1.mpi-sws.org",
        "planetlab1.sics.se",
        "planetlab1.mini.pw.edu.pl",
        "planetlab1.wiwi.hu-berlin.de",
        "planetlab3.mini.pw.edu.pl",
        "planetlab1.eurecom.fr",
        "planet-plc-6.mpi-sws.org",
        "onelab10.pl.sophia.inria.fr",
        "planetlab-2.ing.unimo.it",
        "planetlab2.u-strasbg.fr",
        "ple1.cesnet.cz",
        "prata.mimuw.edu.pl",
        "planetlab1.cesnet.cz",
        "planetlab2.ci.pwr.wroc.pl",
        "planetlab04.mpi-sws.mpg.de",
        "onelab1.warsaw.rd.tp.pl",
        "planetlab4.mini.pw.edu.pl",
        "ple2.cesnet.cz",
        "planet-plc-4.mpi-sws.org",
        "planetlab-2.ida.liu.se",
        "node1pl.planet-lab.telecom-lille1.eu",
        "openlab01.pl.sophia.inria.fr",
        "onelab3.warsaw.rd.tp.pl",
        "planetlab2.uc3m.es",
        "planetlab-1.ing.unimo.it",
        "onelab4.warsaw.rd.tp.pl",
        "utet.ii.uam.es",
        "roti.mimuw.edu.pl",
        "planetlab01.tkn.tu-berlin.de",
        "planet-plc-5.mpi-sws.org",
        "planetlab1.ci.pwr.wroc.pl",
        "ple2.tu.koszalin.pl",
        "planetlab1.fit.vutbr.cz",
        "planet-lab-node2.netgroup.uniroma2.it",
        "planetlab05.mpi-sws.mpg.de",
        "planetlab2.sics.se",
        "planetlab2.s3.kth.se",
        "planetlab-1.ssvl.kth.se",
        "planetlab3.informatik.uni-erlangen.de",
        "plab1-c703.uibk.ac.at",
        "plab2.ple.silweb.pl",
        "plab4.ple.silweb.pl",
        "planetlab2.ifi.uio.no",
        "planet1.inf.tu-dresden.de",
        "plab-2.diegm.uniud.it",
        "planet-lab-node1.netgroup.uniroma2.it",
        "planetlab-2.ssvl.kth.se",
        "onelab6.iet.unipi.it"
    ]

    static def PlanetLab_NC_RAW = [
        "pli1-pa-6.hpl.hp.com",
        "pli1-pa-4.hpl.hp.com",
        "planetslug4.cse.ucsc.edu",
        "planetslug5.cse.ucsc.edu",
        "pli1-pa-5.hpl.hp.com",
        "planetlab1.millennium.berkeley.edu",
        "planetlab2.millennium.berkeley.edu",
        "planetlab8.millennium.berkeley.edu",
        "planetlab7.millennium.berkeley.edu",
        "planetlab12.millennium.berkeley.edu",
        "planetlab5.millennium.berkeley.edu",
        "planetlab16.millennium.berkeley.edu",
        "planetlab10.millennium.berkeley.edu",
        "planetlab13.millennium.berkeley.edu",
        "pl-node-1.csl.sri.com",
        "planetlab-1.calpoly-netlab.net",
        "planetlab-2.calpoly-netlab.net",
        "pl-node-0.csl.sri.com",
        "planet3.cs.ucsb.edu",
        "planet6.cs.ucsb.edu",
        "planet4.cs.ucsb.edu",
        "planet5.cs.ucsb.edu",
        "planetlab1.cs.ucla.edu",
        "planetlab2.cs.ucla.edu",
        "planetlab3.postel.org",
        "planetlab2.postel.org",
        "planetlab1.postel.org",
        "planetlab4.postel.org",
        "planetlab1.ucsd.edu",
        "planetlab2.ucsd.edu",
        "planetlab3.ucsd.edu",
        "pllx1.parc.xerox.com",
        "pllx2.parc.xerox.com",
        "planetlab03.cs.washington.edu",
        "planetlab02.cs.washington.edu",
        "planetlab01.cs.washington.edu",
        "planetlab04.cs.washington.edu",
        "planetlab2.byu.edu",
        "planetlab6.flux.utah.edu",
        "planetlab7.flux.utah.edu",
        "planetlab1.byu.edu",
        "planetlab2.cs.ubc.ca",
        "planetlab2.cs.uoregon.edu",
        "pl2.cs.unm.edu",
        "planetlab4.cs.uoregon.edu",
        "planetlab1.cs.uoregon.edu",
        "pl3.cs.unm.edu",
        "pl4.cs.unm.edu",
        "planetlab3.cs.uoregon.edu",
        "cs-planetlab4.cs.surrey.sfu.ca",
        "cs-planetlab3.cs.surrey.sfu.ca",
        "pl1.planetlab.uvic.ca",
        "planetlab1.uta.edu",
        "planetlab2.uta.edu",
        "ricepl-4.cs.rice.edu",
        "ricepl-5.cs.rice.edu",
        "ricepl-1.cs.rice.edu",
        "ricepl-2.cs.rice.edu",
        "planetlab1.cs.du.edu",
        "planetlab2.cs.du.edu",
        "planetlab-2.cs.uh.edu",
        "planetlab-1.cs.colostate.edu",
        "planetlab1.cs.colorado.edu",
        "planetlab2.cs.colorado.edu"
    ]

    static def PlanetLab_NV_RAW = [
        "planetlab2.georgetown.edu",
        "osiris.planetlab.cs.umd.edu",
        "planetlab1.georgetown.edu",
        "salt.planetlab.cs.umd.edu",
        "plgmu2.ite.gmu.edu",
        "plgmu1.ite.gmu.edu",
        "nodeb.howard.edu",
        "plgmu4.ite.gmu.edu",
        "nodea.howard.edu",
        "planetlab2.jhu.edu",
        "planetlab1.jhu.edu",
        "planetlab1.cnds.jhu.edu",
        "planetlab3.cnds.jhu.edu",
        "planetlab2.cnds.jhu.edu",
        "planetlab4.cnds.jhu.edu",
        "planetlab1.cis.upenn.edu",
        "planetlab2.cis.upenn.edu",
        "plab1.nec-labs.com",
        "plab2.nec-labs.com",
        "planetlab01.sys.virginia.edu",
        "planetlab3.rutgers.edu",
        "orbpl1.rutgers.edu",
        "orbpl2.rutgers.edu",
        "planetlab02.sys.virginia.edu",
        "planetlab4.rutgers.edu",
        "planetlab1.rutgers.edu",
        "planetlab2.rutgers.edu",
        "planetlab-3.cmcl.cs.cmu.edu",
        "planetlab-2.cmcl.cs.cmu.edu",
        "planetlab2.cs.pitt.edu",
        "planetlab-1.cmcl.cs.cmu.edu",
        "planetlab1.cs.pitt.edu",
        "planetlab1.poly.edu",
        "planetlab-01.cs.princeton.edu",
        "planetlab2.poly.edu",
        "planetlab-03.cs.princeton.edu",
        "planetlab1.temple.edu",
        "planetlab3.cs.columbia.edu",
        "planetlab2.cs.columbia.edu",
        "planetlab7.cs.duke.edu",
        "pl1.cs.yale.edu",
        "planetlab1.cs.unc.edu",
        "planetlab-4.eecs.cwru.edu",
        "planetlab-5.eecs.cwru.edu",
        "planetlab2.cs.unc.edu",
        "planetlab-01.vt.nodes.planet-lab.org",
        "planetlab-04.cs.princeton.edu",
        "node2.planetlab.albany.edu",
        "node1.planetlab.albany.edu",
        "planetlab-04.vt.nodes.planet-lab.org",
        "planetlab6.cs.duke.edu",
        "planetlab4.csail.mit.edu",
        "planetlab-02.vt.nodes.planet-lab.org",
        "planetlab-02.bu.edu",
        "planetlab-03.vt.nodes.planet-lab.org",
        "planetlab6.csail.mit.edu",
        "planetlab-01.bu.edu",
        "righthand.eecs.harvard.edu",
        "lefthand.eecs.harvard.edu",
        "earth.cs.brown.edu",
        "planetlab2.temple.edu",
        "planetlab1.cs.umass.edu",
        "planetlab2.cs.umass.edu",
        "planetlab4.williams.edu",
        "pl2.cs.yale.edu",
        "planetlab1.cs.uml.edu",
        "planetlab5.williams.edu",
        "planetlab1.clemson.edu",
        "planetlab1.umassd.edu",
        "planetlab2.clemson.edu",
        "planetlab2.umassd.edu",
        "planetlab1.cnis.nyit.edu",
        "planetlab5.cs.cornell.edu",
        "planetlab1.cs.cornell.edu",
        "planetlab2.cs.cornell.edu",
        "planetlab6.cs.cornell.edu",
        "node-2.mcgillplanetlab.org",
        "planetlab2.williams.edu",
        "node-1.mcgillplanetlab.org",
        "jupiter.cs.brown.edu",
        "planetlab2.cnis.nyit.edu",
        "miranda.planetlab.cs.umd.edu",
        "planetlab01.alucloud.com",
        "planetlab02.alucloud.com",
        "plnode-04.gpolab.bbn.com",
        "plnode-03.gpolab.bbn.com",
        "planetlab1.mnlab.cti.depaul.edu",
        "planetlab-2.cs.uic.edu",
        "planet2.cc.gt.atl.ga.us",
        "planet4.cc.gt.atl.ga.us",
        "planetlab2.mnlab.cti.depaul.edu",
        "pl2.csl.utoronto.ca",
        "planetlab3.eecs.northwestern.edu",
        "planetlab1.citadel.edu",
        "planetlab2.citadel.edu",
        "planetlab2.eecs.ucf.edu",
        "planetlab1.eecs.ucf.edu",
        "saturn.planetlab.carleton.ca",
        "planetlab4.cs.uiuc.edu",
        "planetlab1.cs.uiuc.edu",
        "planetlab6.cs.uiuc.edu",
        "pl1.planet.cs.kent.edu",
        "planetlab5.cs.uiuc.edu",
        "planetlab4.wail.wisc.edu",
        "pl2.rcc.uottawa.ca",
        "pl1.rcc.uottawa.ca",
        "pl2.eecs.utk.edu",
        "planetlabone.ccs.neu.edu",
        "planetlabtwo.ccs.neu.edu",
        "planetlab3.williams.edu",
        "planetlab1.netlab.uky.edu",
        "planetlab2.netlab.uky.edu",
        "75-130-96-13.static.oxfr.ma.charter.com",
        "jupiter.planetlab.carleton.ca",
        "planetlab1.eecs.umich.edu",
        "75-130-96-12.static.oxfr.ma.charter.com",
        "planetlab3.eecs.umich.edu",
        "vn5.cse.wustl.edu",
        "planetlab-2.cse.ohio-state.edu",
        "planetlab3.wail.wisc.edu",
        "planetlab-1.cse.ohio-state.edu",
        "planet12.csc.ncsu.edu",
        "pl1.cis.uab.edu",
        "planetlab2.dtc.umn.edu",
        "pl2.cis.uab.edu",
        "vn4.cse.wustl.edu",
        "planetlab2.cs.purdue.edu",
        "pl2.ucs.indiana.edu",
        "planetlab1.dtc.umn.edu",
        "pl1.ucs.indiana.edu",
        "planet11.csc.ncsu.edu",
        "pluto.cs.brown.edu",
        "planetlab4.eecs.umich.edu",
        "plink.cs.uwaterloo.ca",
        "mtuplanetlab1.cs.mtu.edu",
        "planetlab1.uta.edu",
        "planetlab-6.ece.iastate.edu",
        "planetlab2.cs.okstate.edu",
        "planetlab2.uta.edu",
        "mtuplanetlab2.cs.mtu.edu",
        "planetlab1.cs.okstate.edu",
        "ebb.colgate.edu",
        "planetlab4.tamu.edu",
        "planetlab3.tamu.edu",
        "planetlab2.tamu.edu",
        "planetlab1.tamu.edu",
        "flow.colgate.edu",
        "planetlab1.cs.du.edu",
        "pl1.csl.utoronto.ca",
        "ricepl-2.cs.rice.edu",
        "planetlab2.cs.du.edu",
        "ricepl-4.cs.rice.edu",
        "planetlab-2.cs.colostate.edu",
        "planetlab1.cs.purdue.edu",
        "planetlab-1.cs.colostate.edu",
        "ricepl-5.cs.rice.edu"
    ]

    static def PlanetLab_EU = [
        "planetlab-node-02.ucd.ie",
        "planetlab-node-01.ucd.ie",
        "planetlab-tea.ait.ie",
        "planetlab-coffee.ait.ie",
        "planetlabeu-1.tssg.org",
        "planetlabeu-2.tssg.org",
        "planetlab-2.imperial.ac.uk",
        "planetlab-1.imperial.ac.uk",
        "planetlab-4.imperial.ac.uk",
        "planetlab1.xeno.cl.cam.ac.uk",
        "planetlab3.xeno.cl.cam.ac.uk",
        "planetlab1.aston.ac.uk",
        "planetlab2.xeno.cl.cam.ac.uk",
        "planetlab-3.imperial.ac.uk",
        "planetlab3.cs.st-andrews.ac.uk",
        "planetlab4.cs.st-andrews.ac.uk",
        "planetlab2.aston.ac.uk",
        "pl2.ccsrfi.net",
        "onelab2.info.ucl.ac.be",
        "pl1.ccsrfi.net",
        "planetlab2.cs.vu.nl",
        "planetlab2.ewi.tudelft.nl",
        "planck228ple.test.ibbt.be",
        "planetlab1.cs.vu.nl",
        "chimay.infonet.fundp.ac.be",
        "rochefort.infonet.fundp.ac.be",
        "planet2.l3s.uni-hannover.de",
        "planet1.l3s.uni-hannover.de",
        "planetlab2.extern.kuleuven.be",
        "orval.infonet.fundp.ac.be",
        "planck227ple.test.ibbt.be",
        "planetlab2.montefiore.ulg.ac.be",
        "plewifi.ipv6.lip6.fr",
        "ple2.ipv6.lip6.fr",
        "ple3.ipv6.lip6.fr",
        "host3-plb.loria.fr",
        "ple4.ipv6.lip6.fr",
        "ple6.ipv6.lip6.fr",
        "planetlab1.montefiore.ulg.ac.be",
        "peeramide.irisa.fr",
        "host4-plb.loria.fr",
        "inriarennes1.irisa.fr",
        "lsirextpc01.epfl.ch",
        "planetlab2.thlab.net",
        "planetlab1.thlab.net",
        "iraplab2.iralab.uni-karlsruhe.de",
        "peeramidion.irisa.fr",
        "planetlab1.unineuchatel.ch",
        "planet1.zib.de",
        "inriarennes2.irisa.fr",
        "planet02.hhi.fraunhofer.de",
        "onelab-2.fhi-fokus.de",
        "planetlab4.inf.ethz.ch",
        "planetlab-1.cs.unibas.ch",
        "pl1.bell-labs.fr",
        "dfn-ple1.x-win.dfn.de",
        "onelab-1.fhi-fokus.de",
        "planetlab1.inf.ethz.ch",
        "host1.planetlab.informatik.tu-darmstadt.de",
        "planetlab01.ethz.ch",
        "host2.planetlab.informatik.tu-darmstadt.de",
        "iraplab1.iralab.uni-karlsruhe.de",
        "planetlab3.inf.ethz.ch",
        "planetlab2.wiwi.hu-berlin.de",
        "planetlab1.informatik.uni-goettingen.de",
        "merkur.planetlab.haw-hamburg.de",
        "planetlab2lannion.elibel.tm.fr",
        "planetlab1lannion.elibel.tm.fr",
        "planetlab2.inf.ethz.ch",
        "planetlab-2.cs.unibas.ch",
        "planetlab1.diku.dk",
        "planetlab2.diku.dk",
        "planetlab2.csg.uzh.ch",
        "ple02.fc.univie.ac.at",
        "planetlab2.informatik.uni-goettingen.de",
        "ple01.fc.univie.ac.at",
        "planetlab02.tkn.tu-berlin.de",
        "planetlab1.informatik.uni-leipzig.de",
        "planetlab-1.fokus.fraunhofer.de",
        "planetlab-2.man.poznan.pl",
        "planetlab1.uc3m.es",
        "planetlab1.exp-math.uni-essen.de",
        "planetlab1.csg.uzh.ch",
        "planetlab2.exp-math.uni-essen.de",
        "planetlab01.mpi-sws.mpg.de",
        "openlab02.pl.sophia.inria.fr",
        "planetlab-2.fokus.fraunhofer.de",
        "mars.planetlab.haw-hamburg.de",
        "planetlab2.eecs.jacobs-university.de",
        "planet01.hhi.fraunhofer.de",
        "planetlab2.dit.upm.es",
        "planetlab1.u-strasbg.fr",
        "node2pl.planet-lab.telecom-lille1.eu",
        "planetlab02.mpi-sws.mpg.de",
        "planetlab2.cesnet.cz",
        "planet-plc-1.mpi-sws.org",
        "planetlab1.sics.se",
        "planetlab1.mini.pw.edu.pl",
        "planetlab1.wiwi.hu-berlin.de",
        "planetlab3.mini.pw.edu.pl",
        "planetlab1.eurecom.fr",
        "planet-plc-6.mpi-sws.org",
        "onelab10.pl.sophia.inria.fr",
        "planetlab-2.ing.unimo.it",
        "planetlab2.u-strasbg.fr",
        "ple1.cesnet.cz",
        "prata.mimuw.edu.pl",
        "planetlab1.cesnet.cz",
        "planetlab2.ci.pwr.wroc.pl",
        "planetlab04.mpi-sws.mpg.de",
        "onelab1.warsaw.rd.tp.pl",
        "planetlab4.mini.pw.edu.pl",
        "ple2.cesnet.cz",
        "planet-plc-4.mpi-sws.org",
        "planetlab-2.ida.liu.se",
        "node1pl.planet-lab.telecom-lille1.eu",
        "openlab01.pl.sophia.inria.fr",
        "onelab3.warsaw.rd.tp.pl",
        "planetlab2.uc3m.es",
        "planetlab-1.ing.unimo.it",
        "onelab4.warsaw.rd.tp.pl",
        "utet.ii.uam.es",
        "roti.mimuw.edu.pl",
        "planetlab01.tkn.tu-berlin.de",
        "planet-plc-5.mpi-sws.org",
        "planetlab1.ci.pwr.wroc.pl",
        "ple2.tu.koszalin.pl",
        "planetlab1.fit.vutbr.cz",
        "planet-lab-node2.netgroup.uniroma2.it",
        "planetlab05.mpi-sws.mpg.de",
        "planetlab2.sics.se",
        "planetlab2.s3.kth.se",
        "planetlab-1.ssvl.kth.se",
        "planetlab3.informatik.uni-erlangen.de",
        "plab1-c703.uibk.ac.at",
        "plab2.ple.silweb.pl",
        "plab4.ple.silweb.pl",
        "planetlab2.ifi.uio.no",
        "planet1.inf.tu-dresden.de",
        "plab-2.diegm.uniud.it",
        "planet-lab-node1.netgroup.uniroma2.it",
        "planetlab-2.ssvl.kth.se",
        "onelab6.iet.unipi.it",
        "planetlab2.di.unito.it",
        "planetlab2.upc.es",
        "planet2.elte.hu",
        "planetlab1.tlm.unavarra.es",
        "planetlab3.di.unito.it",
        "scratchy.comlab.bth.se",
        "planetlab-2.research.netlab.hut.fi",
        "onelab2.warsaw.rd.tp.pl",
        "planetlab1.s3.kth.se",
        "planet1.elte.hu",
        "lsirextpc02.epfl.ch",
        "planetlab1.polito.it",
        "plab2-c703.uibk.ac.at",
        "onelab7.iet.unipi.it",
        "plab-1.diegm.uniud.it",
        "planetlab1.ifi.uio.no",
        "planetlab3.hiit.fi",
        "planetlab02.dis.unina.it",
        "planetlab1.tmit.bme.hu",
        "planetlab1.science.unitn.it",
        "planetlab2.rd.tut.fi",
        "planetlab01.dis.unina.it",
        "plab2.create-net.org",
        "planetlab2.tmit.bme.hu",
        "planetlab2.urv.cat",
        "planetlab3.upc.es",
        "planetlab1.rd.tut.fi",
        "itchy.comlab.bth.se",
        "dplanet2.uoc.edu",
        "planetlab1.upc.es"
    ].subList(0, maxPerRegion)

    static def PlanetLab_NC = [
        "pli1-pa-6.hpl.hp.com",
        "pli1-pa-4.hpl.hp.com",
        "planetslug4.cse.ucsc.edu",
        "planetslug5.cse.ucsc.edu",
        "pli1-pa-5.hpl.hp.com",
        //    "planetlab1.millennium.berkeley.edu",
        //    "planetlab2.millennium.berkeley.edu",
        //    "planetlab5.millennium.berkeley.edu",
        "planetlab7.millennium.berkeley.edu",
        //    "planetlab8.millennium.berkeley.edu",
        //    "planetlab12.millennium.berkeley.edu",
        "planetlab16.millennium.berkeley.edu",
        //    "planetlab10.millennium.berkeley.edu",
        //    "planetlab13.millennium.berkeley.edu",
        //    "pl-node-0.csl.sri.com",
        //    "pl-node-1.csl.sri.com",
        "planetlab-1.calpoly-netlab.net",
        "planetlab-2.calpoly-netlab.net",
        "planet3.cs.ucsb.edu",
        "planet4.cs.ucsb.edu",
        //    "planet5.cs.ucsb.edu",
        "planet6.cs.ucsb.edu",
        "planetlab1.cs.ucla.edu",
        "planetlab2.cs.ucla.edu",
        "planetlab1.postel.org",
        "planetlab2.postel.org",
        //    "planetlab3.postel.org",
        "planetlab4.postel.org",
        "planetlab1.ucsd.edu",
        "planetlab2.ucsd.edu",
        "planetlab3.ucsd.edu",
        //    "pllx1.parc.xerox.com",
        //    "pllx2.parc.xerox.com",
        "planetlab01.cs.washington.edu",
        "planetlab02.cs.washington.edu",
        "planetlab03.cs.washington.edu",
        "planetlab04.cs.washington.edu",
        "planetlab6.flux.utah.edu",
        "planetlab7.flux.utah.edu",
        "planetlab1.byu.edu",
        "planetlab2.byu.edu",
        "planetlab2.cs.ubc.ca",
        "planetlab1.cs.uoregon.edu",
        "planetlab2.cs.uoregon.edu",
        "planetlab3.cs.uoregon.edu",
        "planetlab4.cs.uoregon.edu",
        "pl2.cs.unm.edu",
        "pl3.cs.unm.edu",
        "pl4.cs.unm.edu"
    ].subList(0, maxPerRegion)

    static def PlanetLab_NV = [
        "planetlab-2.cmcl.cs.cmu.edu",
        "planetlab-3.cmcl.cs.cmu.edu",
        "planetlab1.cs.pitt.edu",
        "planetlab2.cs.pitt.edu",
        "planetlab-1.cmcl.cs.cmu.edu",
        //    "planetlab1.poly.edu",
        "planetlab2.poly.edu",
        "planetlab-01.cs.princeton.edu",
        "planetlab-03.cs.princeton.edu",
        //    "planetlab-04.cs.princeton.edu",
        "planetlab1.temple.edu",
        "planetlab3.cs.columbia.edu",
        "planetlab2.cs.columbia.edu",
        "planetlab7.cs.duke.edu",
        "pl1.cs.yale.edu",
        "pl2.cs.yale.edu",
        "planetlab-4.eecs.cwru.edu",
        "planetlab-5.eecs.cwru.edu",
        "planetlab1.cs.unc.edu",
        "planetlab2.cs.unc.edu",
        "planetlab-01.vt.nodes.planet-lab.org",
        "planetlab-02.vt.nodes.planet-lab.org",
        "planetlab-03.vt.nodes.planet-lab.org",
        "planetlab-04.vt.nodes.planet-lab.org",
        "node2.planetlab.albany.edu",
        "node1.planetlab.albany.edu",
        "planetlab6.cs.duke.edu",
        "planetlab-01.bu.edu",
        "planetlab-02.bu.edu",
        "planetlab4.csail.mit.edu",
        "planetlab6.csail.mit.edu",
        "righthand.eecs.harvard.edu",
        "lefthand.eecs.harvard.edu",
        "earth.cs.brown.edu",
        "planetlab2.temple.edu",
        "planetlab1.cs.umass.edu",
        "planetlab2.cs.umass.edu",
        "planetlab4.williams.edu",
        "planetlab5.williams.edu",
        "planetlab1.cs.uml.edu",
        "planetlab1.clemson.edu",
        "planetlab2.clemson.edu",
        "planetlab1.umassd.edu",
        "planetlab2.umassd.edu",
        "planetlab1.cnis.nyit.edu",
        "planetlab1.cs.cornell.edu",
        "planetlab2.cs.cornell.edu",
        "planetlab5.cs.cornell.edu",
        "planetlab6.cs.cornell.edu",
        //    "node-2.mcgillplanetlab.org",
        "planetlab2.williams.edu",
        //    "node-1.mcgillplanetlab.org",
        "jupiter.cs.brown.edu",
        "planetlab2.cnis.nyit.edu",
        "miranda.planetlab.cs.umd.edu",
        //    "planetlab01.alucloud.com",
        //    "planetlab02.alucloud.com",
        "plnode-04.gpolab.bbn.com",
        "plnode-03.gpolab.bbn.com",
        "planetlab1.mnlab.cti.depaul.edu",
        "planetlab-2.cs.uic.edu",
        "planet2.cc.gt.atl.ga.us",
        "planet4.cc.gt.atl.ga.us",
        "planetlab2.mnlab.cti.depaul.edu",
        "pl2.csl.utoronto.ca",
        "planetlab3.eecs.northwestern.edu",
        //    "planetlab1.citadel.edu",
        //    "planetlab2.citadel.edu",
        "planetlab2.eecs.ucf.edu",
        "planetlab1.eecs.ucf.edu",
        "saturn.planetlab.carleton.ca",
        "planetlab4.cs.uiuc.edu",
        "planetlab1.cs.uiuc.edu",
        "planetlab6.cs.uiuc.edu",
        "planetlab5.cs.uiuc.edu",
        "planetlab4.wail.wisc.edu"
    ].subList(0, maxPerRegion)
}