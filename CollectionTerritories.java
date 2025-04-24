import java.util.Arrays;

public class CollectionTerritories {
    private Territory myTerre;
    private int timeBeforeInit_αcf_initial;
    private int timeBeforeInit_αcg_initial;
    private int timeBeforeInit_αsf_initial;
    private int timeBeforeInit_αsg_initial;
    private double Kc_initial;
    private double Ks_initial;
    private double[] Kct;
    private double[] Kst;
    private double αc_target;
    private double αs_target;
    private int yearRef;
    private double[] LinearHomeComposter;
    private double[] sigmoide_mcf;
    private double[] sigmoide_mcg;
    private double[] LinearDedicatedCollection;
    private double[] sigmoide_msf;
    private double[] sigmoide_msg;
    private double[] sigmoide_mpg;
    private double αcf_initial;
    private double αcg_initial;
    private double αcf_max;
    private double αcg_max;
    private double αsf_initial;
    private double αsf_max;
    private double αsg_initial;
    private double αsg_max;
    private double b_pf;
    private double b_pg;
    private double αv;
    private double r;
    private int sizePop;
    private double duraImplemCompo;
    private double mc;
    private double duraImplemCollect;
    private double ms;
    private double mpg;
    private double αpg_target;
    double[] P;
    double[] B;
    double[] Bpg;
    double[] Bpf;
    double[] ABP;
    private double[] R;
    private double[] G;
    private double[] αcf;
    private double[] αcg;
    private double[] αvg;
    private double[] αsf;
    private double[] αsg;
    private double[] C_log;
    private double[] C_pop;
    private double[] Bcg;
    private double[] Bcf;
    private double[] Bcf_composted;
    private double[] Bcg_composted;
    double[] Bc_composted;
    private double[] Uc;
    private double[] Ucg;
    private double[] Ucf;
    private double[] sLbis;
    double[] Bv;
    double[] Bsg;
    double[] Bsf;
    double[] Bs_sorted;
    private double[] Bsf_sorted;
    private double[] Bsg_sorted;
    private double[] Usf;
    private double[] Usg;
    private double[] sAa_bis;
    private double[] sAv_bis;
    private double[] Us;
    private double[] sAbis;
    double[] Br;
    private double αpf_target;
    private int subTerritoryName;
    private double[] propPopDesserviCollDA;
    private double[] nbKgCollectHabDesservi;
    private double[] nbKgOMRHab;
    private double[] tauxReductionDechetVert;
    private int ident;

    public(CollectionTerritories(Territory mt, int id) {
        myTerre = mt;
        ident = id;
    }

    public void iterate(int year) {
        LinearHomeComposter[year] = linear(year, duraImplemCompo);
        LinearDedicatedCollection[year] = linear(year, duraImplemCollect);
        if (myTerre.useSocialDynamics) {
            sigmoide_mcf[year] = sigmoide(year + timeBeforeInit_αcf_initial, mc);
            sigmoide_mcg[year] = sigmoide(year + timeBeforeInit_αcg_initial, mc);
            sigmoide_msf[year] = sigmoide(year + timeBeforeInit_αsf_initial, ms);
            sigmoide_msg[year] = sigmoide(year + timeBeforeInit_αsg_initial, ms);
            sigmoide_mpg[year] = sigmoide(year, mpg);
        }
        computeProducedBioWaste(year);
        computeFluxRates(year);
        localCompost(year);
        collect(year);
        recyclingCentre(year);
        residualHouseholdWaste(year);
    }

    public void computeProducedBioWaste(int y) {
        P[y] = P[y - 1] * (1 + r);
        R[y] = αpf_target * myTerre.sigmoideABP[y];
        ABP[y] = R[y] * G[y];
        Bpg[y] = b_pg * (1 - αpg_target * myTerre.sigmoideABP[y]) * P[y];
        Bpf[y] = b_pf * (1 - αpf_target * myTerre.sigmoideABP[y] * myTerre.einit) * P[y];
        B[y] = Bpg[y] + Bpf[y];
    }

    public void computeFluxRates(int y) {
        double trucDa;
        double trucDv;
        αcf[y] = Math.min((αcf_initial + ((1 - αcf_initial) * sigmoide_mcf[y - 1])), 1.0);
        αcg[y] = Math.min((αcg_initial + ((1 - αcg_initial) * sigmoide_mcg[y - 1])), 1.0);
        αsf[y] = αsf_initial + ((1 - αsf_initial) * sigmoide_msf[y]);
        trucDa = αcf[y] + αsf[y];
        if (trucDa > 1.0) {
            αsf[y] = (1 - αcf[y]);
        }
        αsg[y] = αsg_initial + ((αsg_max - αsg_initial) * sigmoide_msg[y]);
        trucDv = αcg[y] + αsg[y];
        if (trucDv > 1.0) {
            αsg[y] = 1.0 - αcg[y];
        }
        αvg[y] = 1 - αcg[y] - αsg[y];
    }

    public void localCompost(int y) {
        Bcg[y] = αcg[y] * Bpg[y];
        Bcf[y] = αcf[y] * Bpf[y];
        if (y == yearRef) {
            Kc_initial = Bcg[y] + Bcf[y];
        }
        Kct[y] = Kc_initial + ((αc_target - Kc_initial) * LinearHomeComposter[y]);
        if ((Bcg[y] + Bcf[y]) > Kct[y]) {
            Uc[y] = Bcg[y] + Bcf[y] - Kct[y];
            Bcg_composted[y] = Math.max(Bcg[y] - Uc[y], 0.0);
            sLbis[y] = Math.max(0.0, (Bcg_composted[y] + Bcf[y] - Kct[y]));
            Bcf_composted[y] = Math.max(Bcf[y] - sLbis[y], 0.0);
            Ucf[y] = Math.min(sLbis[y], Bcf[y]);
            Ucg[y] = Math.min(Uc[y], Bcg[y]);
            Bcg[y] = Bcg_composted[y];
            Bcf[y] = Bcf_composted[y];
        }
        Bc_composted[y] = Bcf[y] + Bcg[y];
    }

    public void collect(int y) {
        Bsg[y] = αsg[y] * Bpg[y];
        Bsf[y] = (αsf[y] * Bpf[y]) + Ucf[y];
        Kst[y] = Ks_initial + ((αs_target - Ks_initial) * LinearDedicatedCollection[y]);
        if ((Bsg[y] + Bsf[y]) > Kst[y]) {
            Us[y] = Bsf[y] + Bsg[y] - Kst[y];
            Bsg_sorted[y] = Math.max(Bsg[y] - Us[y], 0.0);
            sAbis[y] = Math.max(0.0, (Bsf[y] + Bsg_sorted[y] - Kst[y]));
            Bsf_sorted[y] = Math.max(Bsf[y] - sAbis[y], 0.0);
            Usg[y] = Math.min(Us[y], Bsg[y]);
            Usf[y] = Math.min(sAbis[y], Bsf[y]);
            Bsg[y] = Bsg_sorted[y];
            Bsf[y] = Bsf_sorted[y];
        }
        Bs_sorted[y] = Bsg[y] + Bsf[y];
    }

    public void recyclingCentre(int y) {
        Bv[y] = αvg[y] * Bpg[y] + Ucg[y] + Usg[y];
    }

    public void residualHouseholdWaste(int y) {
        Br[y] = (1 - αcf[y] - αsf[y]) * Bpf[y] + Usf[y];
        if (Br[y] < 0) {
            System.err.println("Error: Negative residual waste");
        }
    }

    public double sigmoide(double x, double ti) {
        double t = Math.pow(x, 5);
        double z = t / (t + Math.pow(ti, 5));
        return z;
    }

    public double linear(double t, double duration) {
        return Math.min(t / duration, 1.0);
    }

    public int calculateTimeBeforeInit(double alpha_base, double ti) {
        int timeBeforeInit = 0;
        if (alpha_base > 0) {
            double sigmoideValue = sigmoide(timeBeforeInit, ti);
            while (sigmoideValue < alpha_base) {
                timeBeforeInit++;
                sigmoideValue = sigmoide(timeBeforeInit, ti);
            }
        }
        return timeBeforeInit;
    }

    public void init(int sizeData, double[] params, int refYear) {
        yearRef = refYear;
        subTerritoryName = (int) params[0];
        duraImplemCompo = params[1];
        duraImplemCollect = params[2];
        mc = params[3];
        ms = params[4];
        b_pf = params[5];
        b_pg = params[6];
        αcf_initial = params[7];
        αcg_initial = params[8];
        αsf_initial = params[9];
        αsf_max = params[10];
        αcf_max = params[11];
        αcg_max = params[12];
        αsg_initial = params[13];
        αsg_max = params[14];
        Kc_initial = params[15];
        αc_target = params[16];
        Ks_initial = params[17];
        αs_target = params[18];
        sizePop = (int) params[19];
        r = params[20];
        mpg = params[21];
        αpg_target = params[22];
        αpf_target = params[23];
        timeBeforeInit_αcf_initial = calculateTimeBeforeInit(αcf_initial, mc);
        timeBeforeInit_αcg_initial = calculateTimeBeforeInit(αcg_initial, mc);
        timeBeforeInit_αsf_initial = calculateTimeBeforeInit(αsf_initial, ms);
        timeBeforeInit_αsg_initial = calculateTimeBeforeInit(αsg_initial, ms);
        P = new double[sizeData];
        Arrays.fill(P, 0.0);
        P[0] = sizePop;
        R = new double[sizeData];
        Arrays.fill(R, 0.0);
        ABP = new double[sizeData];
        Arrays.fill(ABP, 0.0);
        G = new double[sizeData];
        Arrays.fill(G, 0.0);
        B = new double[sizeData];
        Arrays.fill(B, 0.0);
        Bpg = new double[sizeData];
        Arrays.fill(Bpg, 0.0);
        Bpf = new double[sizeData];
        Arrays.fill(Bpf, 0.0);
        αcf = new double[sizeData];
        Arrays.fill(αcf, 0.0);
        αcg = new double[sizeData];
        Arrays.fill(αcg, 0.0);
        αvg = new double[sizeData];
        Arrays.fill(αvg, 0.0);
        C_log = new double[sizeData];
        Arrays.fill(C_log, 0.0);
        C_pop = new double[sizeData];
        Arrays.fill(C_pop, 0.0);
        Bc_composted = new double[sizeData];
        Arrays.fill(Bc_composted, 0.0);
        Bcg = new double[sizeData];
        Arrays.fill(Bcg, 0.0);
        Bcf = new double[sizeData];
        Arrays.fill(Bcf, 0.0);
        Uc = new double[sizeData];
        Arrays.fill(Uc, 0.0);
        Ucf = new double[sizeData];
        Arrays.fill(Ucf, 0.0);
        Ucg = new double[sizeData];
        Arrays.fill(Ucg, 0.0);
        Bcg_composted = new double[sizeData];
        Arrays.fill(Bcg_composted, 0.0);
        Bcf_composted = new double[sizeData];
        Arrays.fill(Bcf_composted, 0.0);
        sLbis = new double[sizeData];
        Arrays.fill(sLbis, 0.0);
        Bv = new double[sizeData];
        Arrays.fill(Bv, 0.0);
        Usg = new double[sizeData];
        Arrays.fill(Usg, 0.0);
        Br = new double[sizeData];
        Arrays.fill(Br, 0.0);
        Kst = new double[sizeData];
        Arrays.fill(Kst, 0.0);
        Kct = new double[sizeData];
        Arrays.fill(Kct, 0.0);
        LinearHomeComposter = new double[sizeData];
        Arrays.fill(LinearHomeComposter, 0.0);
        sigmoide_mcf = new double[sizeData];
        Arrays.fill(sigmoide_mcf, 0.0);
        sigmoide_mcg = new double[sizeData];
        Arrays.fill(sigmoide_mcg, 0.0);
        LinearDedicatedCollection = new double[sizeData];
        Arrays.fill(LinearDedicatedCollection, 0.0);
        sigmoide_msf = new double[sizeData];
        Arrays.fill(sigmoide_msf, 0.0);
        sigmoide_msg = new double[sizeData];
        Arrays.fill(sigmoide_msg, 0.0);
        sigmoide_mpg = new double[sizeData];
        Arrays.fill(sigmoide_mpg, 0.0);
        Bsg = new double[sizeData];
        Arrays.fill(Bsg, 0.0);
        Bsf = new double[sizeData];
        Arrays.fill(Bsf, 0.0);
        Bsf_sorted = new double[sizeData];
        Arrays.fill(Bsf_sorted, 0.0);
        Bsg_sorted = new double[sizeData];
        Arrays.fill(Bsg_sorted, 0.0);
        Bs_sorted = new double[sizeData];
        Arrays.fill(Bs_sorted, 0.0);
        Us = new double[sizeData];
        Arrays.fill(Us, 0.0);
        sAbis = new double[sizeData];
        Arrays.fill(sAbis, 0.0);
        Usf = new double[sizeData];
        Arrays.fill(Usf, 0.0);
        propPopDesserviCollDA = new double[sizeData];
        Arrays.fill(propPopDesserviCollDA, 0.0);
        nbKgCollectHabDesservi = new double[sizeData];
        Arrays.fill(nbKgCollectHabDesservi, 0.0);
        nbKgOMRHab = new double[sizeData];
        Arrays.fill(nbKgOMRHab, 0.0);
        tauxReductionDechetVert = new double[sizeData];
        Arrays.fill(tauxReductionDechetVert, 0.0);
        αsg = new double[sizeData];
        Arrays.fill(αsg, 0.0);
        αsf = new double[sizeData];
        Arrays.fill(αsf, 0.0);
        Bpf[0] = b_pf * P[0];
        Bpg[0] = b_pg * P[0];
        Bcg[0] = Bpg[0] * αcg_initial;
        Bcf[0] = Bpf[0] * αcf_initial;
        Bsf[0] = Bpf[0] * αsf_initial;
        Bsg[0] = Bpg[0] * αsg_initial;
        Bv[0] = Bpg[0] - Bcg[0] - Bsg[0];
        Br[0] = Bpf[0] - Bcf[0] - Bsf[0];
    }
}
