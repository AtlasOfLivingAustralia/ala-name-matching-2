package au.org.ala.names.generated;

public class GrassInference {
  public GrassParameters parameters;

  public double infer(Evidence evidence, double c$rain) {
    double nc$rain = 1.0 - c$rain;
    double c$sprinkler = evidence.isT$sprinkler();
    double nc$sprinkler = evidence.isF$sprinkler();
    double c$wet = 0;
    double nc$wet = 0;
    // Ignoring non-base sprinkler
    if (evidence.e$wet == null || evidence.e$wet) {
      c$wet += this.parameters.derived_t_tt$wet * c$sprinkler * c$rain;
      c$wet += this.parameters.derived_t_tf$wet * c$sprinkler * nc$rain;
      c$wet += this.parameters.derived_t_ft$wet * nc$sprinkler * c$rain;
      c$wet += this.parameters.derived_t_ff$wet * nc$sprinkler * nc$rain;
    }
    if (evidence.e$wet == null || !evidence.e$wet) {
      nc$wet += this.parameters.derived_f_tt$wet * c$sprinkler * c$rain;
      nc$wet += this.parameters.derived_f_tf$wet * c$sprinkler * nc$rain;
      nc$wet += this.parameters.derived_f_ft$wet * nc$sprinkler * c$rain;
      nc$wet += this.parameters.derived_f_ff$wet * nc$sprinkler * nc$rain;
    }
    return (c$wet + nc$wet) * (this.parameters.prior_t$rain * c$rain + this.parameters.prior_f$rain * nc$rain);
  }

  public double probability(Evidence evidence) {
    double p;
    double ph = 0.0;
    double pe = 0.0;

    p = evidence.isT$rain() * this.infer(evidence, 1.0);
    ph += p;
    pe += p;
    p = evidence.isF$rain() * this.infer(evidence, 0.0);
    pe += p;
    return pe == 0.0 ? 0.0 : ph / pe;
  }

  public static class Evidence {
    public Boolean e$rain;
    public Boolean e$sprinkler;
    public Boolean e$wet;

    public double isT$rain() {
      return this.e$rain == null || this.e$rain ? 1.0 : 0.0;
    }

    public double isF$rain() {
      return this.e$rain == null || !this.e$rain ? 1.0 : 0.0;
    }

    public double isT$sprinkler() {
      return this.e$sprinkler == null || this.e$sprinkler ? 1.0 : 0.0;
    }

    public double isF$sprinkler() {
      return this.e$sprinkler == null || !this.e$sprinkler ? 1.0 : 0.0;
    }

    public double isT$wet() {
      return this.e$wet == null || this.e$wet ? 1.0 : 0.0;
    }

    public double isF$wet() {
      return this.e$wet == null || !this.e$wet ? 1.0 : 0.0;
    }

  }
}