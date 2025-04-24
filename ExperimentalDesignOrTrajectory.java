import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.util.Arrays;

public class ExperimentalDesignOrTrajectory {
    private boolean printTrajectory = false;
    private PrintStream console = System.out;
    private PrintStream ps;
    private FileOutputStream fic;
    private static String nomFichier;
    private double[] paramsTerritory;
    private int nameExpe;
    private int nbYearsSimu;
    private int nbSubterritories;
    private double[][] paramsSubTerritories;
    private Territory myDyn;

    public ExperimentalDesignOrTrajectory(String param, int ligne, int fs, boolean entete) {
        ecritureResultats("fichierResultat.txt");
        console.println("launching the simulation with file " + param);
        lectureEntree(param, ligne, fs, entete);
        System.err.println("The simulation is finished.");
        try {
            fic.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("problem closing the file");
        }
    }

    public ExperimentalDesignOrTrajectory(String param, String nomFichR, int ligne, int fs, boolean entete) {
        ecritureResultats(nomFichR);
        console.println("launching the simulation with file " + param + " the result file is " + nomFichR);
        lectureEntree(param, ligne, fs, entete);
        try {
            fic.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("problem closing the file");
        }
    }

    private String paramFileName;

    public void lectureEntree(String fileName, int ligne, int frequenceSauvegarde, boolean entete) {
        try {
            paramFileName = fileName;
            FileReader file = new FileReader(fileName);
            StreamTokenizer st = new StreamTokenizer(file);
            while (st.lineno() < ligne) {
                st.nextToken();
            }
            int nbParamTerritory = 8;
            paramsTerritory = new double[nbParamTerritory];
            Arrays.fill(paramsTerritory, 0.0);
            nameExpe = (int) st.nval;
            paramsTerritory[0] = (double) nameExpe;
            st.nextToken();
            paramsTerritory[1] = (double) st.nval;
            st.nextToken();
            paramsTerritory[2] = (double) st.nval;
            st.nextToken();
            nbYearsSimu = (int) st.nval;
            paramsTerritory[3] = (double) nbYearsSimu;
            st.nextToken();
            nbSubterritories = (int) st.nval;
            paramsTerritory[4] = (double) nbSubterritories;
            st.nextToken();
            paramsTerritory[5] = (double) st.nval;
            st.nextToken();
            paramsTerritory[6] = (double) st.nval;
            st.nextToken();
            paramsTerritory[7] = (double) st.nval;
            st.nextToken();
            int nbParamToDescribeSubTerritory = 24;
            paramsSubTerritories = new double[nbSubterritories][nbParamToDescribeSubTerritory];
            double[] paramSubT;
            for (int a = 0; a < nbSubterritories; a++) {
                paramSubT = new double[nbParamToDescribeSubTerritory];
                Arrays.fill(paramSubT, 0.0);
                paramSubT[0] = (double) st.nval;
                st.nextToken();
                paramSubT[1] = (double) st.nval;
                st.nextToken();
                paramSubT[2] = (double) st.nval;
                st.nextToken();
                paramSubT[3] = (double) st.nval;
                st.nextToken();
                paramSubT[4] = (double) st.nval;
                st.nextToken();
                paramSubT[5] = (double) st.nval;
                st.nextToken();
                paramSubT[6] = (double) st.nval;
                st.nextToken();
                paramSubT[7] = (double) st.nval;
                st.nextToken();
                paramSubT[8] = (double) st.nval;
                st.nextToken();
                paramSubT[9] = (double) st.nval;
                st.nextToken();
                paramSubT[10] = (double) st.nval;
                st.nextToken();
                paramSubT[11] = (double) st.nval;
                st.nextToken();
                paramSubT[12] = (double) st.nval;
                st.nextToken();
                paramSubT[13] = (double) st.nval;
                st.nextToken();
                paramSubT[14] = (double) st.nval;
                st.nextToken();
                paramSubT[15] = (double) st.nval;
                st.nextToken();
                paramSubT[16] = (double) st.nval;
                st.nextToken();
                paramSubT[17] = (double) st.nval;
                st.nextToken();
                paramSubT[18] = (double) st.nval;
                st.nextToken();
                paramSubT[19] = (double) st.nval;
                st.nextToken();
                paramSubT[20] = (double) st.nval;
                st.nextToken();
                paramSubT[21] = (double) st.nval;
                st.nextToken();
                paramSubT[22] = (double) st.nval;
                st.nextToken();
                paramSubT[23] = (double) st.nval;
                st.nextToken();
                for (int b = 0; b < nbParamToDescribeSubTerritory; b++) {
                    paramsSubTerritories[a][b] = paramSubT[b];
                }
                paramSubT = null;
            }
            if (entete & !printTrajectory) {
                System.out.print("nameOftheParametersFile" + ";");
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("NameSubTerritory" + ";");
                    System.out.print("alpha1ObjDA" + i + ";");
                    System.out.print("alpha1ObjDV" + i + ";");
                    System.out.print("tiPracticCompostLocal" + i + ";");
                    System.out.print("alpha2ObjDA" + i + ";");
                    System.out.print("alpha2ObjDV" + i + ";");
                    System.out.print("tiPracticTri" + i + ";");
                }
                System.out.print("t" + ";");
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("nbKgCollectHabDesserviSubTerrit" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("partPopDesserviCollDAlim" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("nbKgDechetAlimOMRByHabitant" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("alpha1Da" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("alpha1Dv" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("alpha2Da" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("alpha2Dv" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("K1courant" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("Kacourant" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("TauxEvolDechetsVertsDansDechetterie" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("Bv" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("Av" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("sAv" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("Dv" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("Lv" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("sLv" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("Ba" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("Aa" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("sAa" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("La" + i + ";");
                }
                for (int i = 0; i < nbSubterritories; i++) {
                    System.out.print("sLa" + i + ";");
                }
                System.out.print("nbKgDechetAlimOMRByHabitantGlobal" + ";");
                System.out.print("sizePop" + ";");
                System.out.print("DiminutionGaspillageAlimentaire" + ";");
                System.out.print("ProducedBioWasteTotale" + ";");
                System.out.print("ProducedBioWasteAlimentaire" + ";");
                System.out.print("ProducedBioWasteVerts" + ";");
                System.out.print("OMR" + ";");
                System.out.print("compostage local" + ";");
                System.out.print("déchetterie" + ";");
                System.out.print("qteEntrantMethaniseur" + ";");
                System.out.print("qteTraiteesParMethaniseur" + ";");
                System.out.print("compostage professionnel" + ";");
                System.out.print("incinerateur" + ";");
                System.out.print("tauxValorisationbiowaste" + ";");
                System.out.print("nbSolutionsForFoodPerHab" + ";");
                System.out.print("nbSolutionsForGreenPerHab" + ";");
                System.out.print("tauxReductionDechetsVerts" + ";");
                System.out.print("increaseOfMethaniseFoodwaste" + ";");
                System.out.print("multiplicateurVolumeBiodechetOMR" + ";");
                System.out.print("totalIntentionForFoodWaste" + ";");
                System.out.print("totalIntentionForGreenWasteWithoutDechetterie" + ";");
                System.out.print("valorizationRateSufficientAt12" + ";");
                System.out.print("oneSolutionAtLeastPerHabForFoodAt6" + ";");
                System.out.print("oneSolutionAtLeastPerHabForGreen" + ";");
                System.out.print("tauxDiminutionDechetsVertsCorrect" + ";");
                System.out.print("correctIncreaseMethaniseFoodwasteCorrect" + ";");
                System.out.print("diminutionVolumeBiodechetOMR" + ";");
                System.out.print("checkCoherenceFluxStage1" + ";");
                System.out.print("checkCoherenceFluxStage2" + ";");
                System.out.print("year" + ";");
                System.out.print("EvolVolumeDADansOMR" + ";");
                System.out.print("NombreObjectifsRespectes" + ";");
                System.out.print("4ObjectifsAtteints" + ";");
                System.out.println();
                entete = false;
            }
            if (!printTrajectory) {
                double stepToExplore = 0.002;
                double[] testObjectifCompostLocalDa = {1.0};
                double[] testObjectifCompostLocalDv = {1.0};
                double[] testObjectifTriCollecteDa = {1.0};
                double[] testObjectifTriCollecteDv = {1.0};
                double[] vitesseAdoptPraticCompost = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
                double[] vitesseAdoptPracticCollecte = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
                for (int i = 0; i < testObjectifCompostLocalDa.length; i++) {
                    for (int z = 0; z < nbSubterritories; z++) {
                        paramsSubTerritories[z][11] = testObjectifCompostLocalDa[i];
                    }
                    for (int x = 0; x < testObjectifCompostLocalDv.length; x++) {
                        for (int z = 0; z < nbSubterritories; z++) {
                            paramsSubTerritories[z][12] = testObjectifCompostLocalDv[x];
                        }
                        for (int k = 0; k < vitesseAdoptPraticCompost.length; k++) {
                            for (int z = 0; z < nbSubterritories; z++) {
                                paramsSubTerritories[z][3] = vitesseAdoptPraticCompost[k];
                            }
                            for (int y = 0; y < testObjectifTriCollecteDa.length; y++) {
                                paramsSubTerritories[0][10] = testObjectifTriCollecteDa[y];
                                paramsSubTerritories[1][10] = testObjectifTriCollecteDa[y];
                                for (int j = 0; j < testObjectifTriCollecteDv.length; j++) {
                                    paramsSubTerritories[1][14] = testObjectifTriCollecteDv[j];
                                    for (int l = 0; l < vitesseAdoptPracticCollecte.length; l++) {
                                        paramsSubTerritories[0][4] = vitesseAdoptPracticCollecte[l];
                                        paramsSubTerritories[1][4] = vitesseAdoptPracticCollecte[l];
                                        myDyn = null;
                                        myDyn = new Territory(nbYearsSimu, nbSubterritories, paramsTerritory, paramsSubTerritories, printTrajectory);
                                        indicatorsObjectives(nbYearsSimu);
                                        ecritureResultatsComputed(nbYearsSimu);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                myDyn = null;
                myDyn = new Territory(nbYearsSimu, nbSubterritories, paramsTerritory, paramsSubTerritories, printTrajectory);
                indicatorsObjectives(nbYearsSimu);
                ecritureTrajectoire(nbYearsSimu);
            }
        } catch (Exception e) {
            System.err.println("reading error : " + e.toString());
            e.printStackTrace();
            System.out.println("reading error : " + e.toString());
        }
    }

    public void ecritureResultats(String nameResult) {
        try {
            fic = new FileOutputStream(nameResult, true);
            ps = new PrintStream(fic);
            System.setOut(ps);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("I have a problem writing the results");
        }
    }

    public void ecritureResultatsComputed(int nbYears) {
        // Implementation for writing results
    }

    public void ecritureTrajectoire(int nbYears) {
        // Implementation for writing trajectory
    }

    public void indicatorsObjectives(int nbYears) {
        // Implementation for computing indicators
    }
}
