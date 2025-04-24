import java.util.Arrays;

public class LargeScaleInfrastructure {
    private double αm_max;
    private double αcc_max;
    private double αi_max;
    double[] Bi;
    double[] Bmg;
    double[] Bmf;
    double[] Bm;
    private double[] Um;
    private double[] sMbis;
    private double[] Bmf_methanised;
    private double[] Bmg_methanised;
    private double[] Umf;
    private double[] Umg;
    private double[] sF;
    private double[] sFv_meth;
    private double[] sFv_inci;
    double[] Bcc;

    public LargeScaleInfrastructure() {}

    public void init(int sizeData, int KMethaniseur, int KIncinerator, int KnbCompostPro) {
        αm_max = KMethaniseur;
        αcc_max = KnbCompostPro;
        αi_max = KIncinerator;
        Bi = new double[sizeData];
        Arrays.fill(Bi, 0.0);
        Bmf = new double[sizeData];
        Arrays.fill(Bmf, 0.0);
        Bmg = new double[sizeData];
        Arrays.fill(Bmg, 0.0);
        Bm = new double[sizeData];
        Arrays.fill(Bm, 0.0);
        Um = new double[sizeData];
        Arrays.fill(Um, 0.0);
        sMbis = new double[sizeData];
        Arrays.fill(sMbis, 0.0);
        Bmf_methanised = new double[sizeData];
        Arrays.fill(Bmf_methanised, 0.0);
        Bmg_methanised = new double[sizeData];
        Arrays.fill(Bmg_methanised, 0.0);
        Umf = new double[sizeData];
        Arrays.fill(Umf, 0.0);
        Umg = new double[sizeData];
        Arrays.fill(Umg, 0.0);
        sF = new double[sizeData];
        Arrays.fill(sF, 0.0);
        sFv_meth = new double[sizeData];
        Arrays.fill(sFv_meth, 0.0);
        sFv_inci = new double[sizeData];
        Arrays.fill(sFv_inci, 0.0);
        Bcc = new double[sizeData];
        Arrays.fill(Bcc, 0.0);
    }

    public void iterate(int year, double fluxBg, double fluxBf, double fluxDv, double fluxBr) {
        computeMethanisation(year, fluxBg, fluxBf);
        computeCompostPlatform(year, fluxDv);
        computeIncinerator(year, fluxBr);
    }

    public void computeMethanisation(int y, double fluxBg, double fluxBf) {
        if (αm_max > 0) {
            Bmg[y] = fluxBg;
            Bmf[y] = fluxBf;
            Bm[y] = Bmg[y] + Bmf[y];
            if ((Bmg[y] + Bmf[y]) > αm_max) {
                Um[y] = Bmf[y] + Bmg[y] - αm_max;
                Bmg_methanised[y] = Math.max(Bmg[y] - Um[y], 0.0);
                sMbis[y] = Math.max(0.0, Bmf[y] + Bmg_methanised[y] - αm_max);
                Bmf_methanised[y] = Math.max(Bmf[y] - sMbis[y], 0.0);
                Umg[y] = Math.min(Um[y], Bmg[y]);
                Umf[y] = Math.min(sMbis[y], Bmf[y]);
                Bm[y] = Bmg_methanised[y] + Bmf_methanised[y];
            }
        }
    }

    public void computeCompostPlatform(int y, double fluxDv) {
        Bcc[y] = fluxDv + Umg[y] + Umf[y];
        if (Bcc[y] > αcc_max) {
            sF[y] = Bcc[y] - αcc_max;
            sFv_meth[y] = Math.min(sF[y], αm_max - Bm[y]);
            Bm[y] += sFv_meth[y];
            sFv_inci[y] = Math.max(0.0, (sF[y] - sFv_meth[y]));
            Bcc[y] = Math.max((Bcc[y] - sFv_inci[y] - sFv_meth[y]), 0.0);
        }
    }

    public void computeIncinerator(int y, double fluxBr) {
        Bi[y] = fluxBr + sFv_inci[y];
    }
}
