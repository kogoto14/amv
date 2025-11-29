package dev.aulait.amv.domain.process.extractor;

import dev.aulait.amv.domain.extractor.java.ExtractionLogic;
import dev.aulait.amv.domain.extractor.java.ExtractionService;
import dev.aulait.amv.domain.extractor.java.JavaExtractorFactory;
import dev.aulait.amv.domain.extractor.java.MetadataAdjusterFactory;
import dev.aulait.amv.domain.extractor.java.MetadataExtractorFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ExtractionServiceConfig {

  @Produces
  public ExtractionService extractionService(
      MetadataExtractorFactory factory, ExtractionLogic logic) {
    return new ExtractionService(factory, logic);
  }

  @Produces
  public ExtractionLogic extractionLogic() {
    return new ExtractionLogic();
  }

  @Produces
  public MetadataExtractorFactory metadataExtractorFactory(
      MetadataAdjusterFactory adjusterFactory) {
    return new JavaExtractorFactory(adjusterFactory);
  }

  @Produces
  public MetadataAdjusterFactory metadataAdjusterFactory() {
    return new MetadataAdjusterFactory();
  }
}
