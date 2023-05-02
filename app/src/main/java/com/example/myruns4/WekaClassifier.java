package com.example.myruns4;

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N545bdc8b0(i);
        return p;
    }
    static double N545bdc8b0(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 6.355508) {
            p = WekaClassifier.N2baf8d021(i);
        } else if (((Double) i[64]).doubleValue() > 6.355508) {
            p = 2;
        }
        return p;
    }
    static double N2baf8d021(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 18.205911) {
            p = WekaClassifier.N3292e59a2(i);
        } else if (((Double) i[0]).doubleValue() > 18.205911) {
            p = WekaClassifier.N49deceb44(i);
        }
        return p;
    }
    static double N3292e59a2(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 12.090795) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 12.090795) {
            p = WekaClassifier.N436bedd63(i);
        }
        return p;
    }
    static double N436bedd63(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 4.744151) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 4.744151) {
            p = 0;
        }
        return p;
    }
    static double N49deceb44(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 4.355784) {
            p = WekaClassifier.N1e4e8d105(i);
        } else if (((Double) i[7]).doubleValue() > 4.355784) {
            p = WekaClassifier.N7294a3a29(i);
        }
        return p;
    }
    static double N1e4e8d105(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 1;
        } else if (((Double) i[16]).doubleValue() <= 1.496086) {
            p = 1;
        } else if (((Double) i[16]).doubleValue() > 1.496086) {
            p = WekaClassifier.N3b5515766(i);
        }
        return p;
    }
    static double N3b5515766(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 2;
        } else if (((Double) i[10]).doubleValue() <= 2.250664) {
            p = WekaClassifier.N27cc4a977(i);
        } else if (((Double) i[10]).doubleValue() > 2.250664) {
            p = 1;
        }
        return p;
    }
    static double N27cc4a977(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= 6.920065) {
            p = WekaClassifier.N18589bb48(i);
        } else if (((Double) i[5]).doubleValue() > 6.920065) {
            p = 1;
        }
        return p;
    }
    static double N18589bb48(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 10.587789) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 10.587789) {
            p = 2;
        }
        return p;
    }
    static double N7294a3a29(Object []i) {
        double p = Double.NaN;
        if (i[25] == null) {
            p = 2;
        } else if (((Double) i[25]).doubleValue() <= 0.320553) {
            p = 2;
        } else if (((Double) i[25]).doubleValue() > 0.320553) {
            p = WekaClassifier.N61f9f62310(i);
        }
        return p;
    }
    static double N61f9f62310(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 13.047825) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() > 13.047825) {
            p = WekaClassifier.N24a4807911(i);
        }
        return p;
    }
    static double N24a4807911(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 20.687854) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 20.687854) {
            p = 2;
        }
        return p;
    }
}
