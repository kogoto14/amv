package dev.aulait.amv.domain.extractor.java;

import java.util.List;
import java.util.ServiceLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetadataAdjusterFactory {

  @Getter private List<MetadataAdjuster> adjusters;

  public MetadataAdjusterFactory() {
    init();
  }

  public void init() {
    ServiceLoader<MetadataAdjuster> loader = ServiceLoader.load(MetadataAdjuster.class);
    adjusters = loader.stream().map(ServiceLoader.Provider::get).toList();
    log.info("Loaded {} MetadataAdjusters {}", adjusters.size(), adjusters);
  }
}
