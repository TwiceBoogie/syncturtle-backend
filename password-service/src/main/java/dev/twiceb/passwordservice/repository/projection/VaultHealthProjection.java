package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.model.EncryptionKey;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface VaultHealthProjection {

    Long getId(); // this is the userId;

    List<EncryptionKey> getEncryptionKeys();

    @Value("#{@passwordServiceHelper.countTotalPasswords(target.encryptionKeys)}")
    int getTotalPasswords();

    @Value("#{@passwordServiceHelper.countTotalWeakPasswords(target.encryptionKeys)}")
    int getTotalWeakPasswords();

    @Value("#{@passwordServiceHelper.countTotalReusedPassword(target.id)}")
    int getTotalReusePasswordCount();

    @Value("#{@passwordServiceHelper.getAveragePasswordComplexityScore(target.encryptionKeys)}")
    double getAverageEntropyScore();
    // static double getReuseScoreWeight() {
    // return 0.5;
    // }
    // static double getWeakPasswordScoreWeight() {
    // return 0.3;
    // }
    // static double getEntropyScoreWeight() {
    // return 0.1;
    // }
    // List<Keychain> getKeychains();
    // List<PasswordReuseStatistic> getReuseStatistics();
    // default int getTotalPasswords() {
    // return getKeychains().size();
    // }
    //
    // default double getAverageEntropyScore() {
    // List<Keychain> keychains = getKeychains();
    // double count = 0;
    // for (Keychain keychain : keychains) {
    // count += keychain.getComplexityMetric().getEntropy();
    // }
    // return count / getTotalPasswords();
    // }
    // default int getTotalWeakPasswords() {
    // List<Keychain> keychains = getKeychains();
    // int count = 0;
    // for (Keychain keychain : keychains) {
    // if (keychain.getComplexityMetric().getEntropy() <= 50) {
    // count++;
    // }
    // }
    // return count;
    // }
    // default int getTotalReuseStats() {
    // List<PasswordReuseStatistic> reuseStatistics = getReuseStatistics();
    // int count = 0;
    // for (PasswordReuseStatistic reuse : reuseStatistics) {
    // if (reuse.getReuseCount() > 0) {
    // count += reuse.getReuseCount();
    // }
    // }
    // return count;
    // }
    // default double getVaultHealthPercentage() {
    // List<Keychain> keychains = getKeychains();
    // if (keychains.size() < 5) {
    // return 0;
    // }
    // // 1.) calculate individual factors score
    // int reusedScore = (getTotalReuseStats() / keychains.size()) * 100;
    // int weakScore = (getTotalWeakPasswords() / keychains.size()) * 100;
    // // 2.) calculate weighted average of scores
    // double weightedReusedScore = reusedScore * getReuseScoreWeight();
    // double weightedWeakScore = weakScore * getWeakPasswordScoreWeight();
    // double weightedEntropyScore = getAverageEntropyScore() *
    // getEntropyScoreWeight();
    // // 3.) calculate negative impact on password health
    // double negative_impact = weightedReusedScore + weightedWeakScore +
    // weightedEntropyScore;
    //
    // return 100 - negative_impact;
    // }
}
