package com.packagedfood.recommendation.config;

import com.packagedfood.recommendation.entity.Additive;
import com.packagedfood.recommendation.entity.HealthCondition;
import com.packagedfood.recommendation.entity.HealthConditionRule;
import com.packagedfood.recommendation.repository.AdditiveRepository;
import com.packagedfood.recommendation.repository.HealthConditionRepository;
import com.packagedfood.recommendation.repository.HealthConditionRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeBaseDataInitializer {

    @Bean
    CommandLineRunner initializeKnowledgeBase(
            HealthConditionRepository healthConditionRepository,
            HealthConditionRuleRepository ruleRepository,
            AdditiveRepository additiveRepository) {

        return args -> {

            if (healthConditionRepository.count() == 0) {

                HealthCondition diabetes =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "Diabetes",
                                        "A metabolic condition in which blood glucose regulation is impaired. "
                                                + "For food assessment, carbohydrate and sugar content are important "
                                                + "factors, together with serving size and overall dietary context."
                                ));

                HealthCondition hypertension =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "Hypertension",
                                        "High blood pressure. Sodium intake is an important dietary factor "
                                                + "when assessing packaged foods."
                                ));

                HealthCondition cholesterol =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "High Cholesterol",
                                        "A condition involving elevated blood lipids. Saturated fat "
                                                + "and overall dietary fat are relevant when assessing foods."
                                ));

                HealthCondition obesity =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "Obesity",
                                        "A chronic condition involving excess body fat. Energy density, "
                                                + "portion size, sugars and fat can be relevant to food assessment."
                                ));

                HealthCondition kidneyDisease =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "Chronic Kidney Disease",
                                        "A chronic condition affecting kidney function. Nutritional "
                                                + "assessment can involve sodium, protein and other nutrients "
                                                + "depending on disease stage and clinical advice."
                                ));

                HealthCondition heartDisease =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "Heart Disease",
                                        "Cardiovascular disease for which dietary patterns may consider "
                                                + "sodium, saturated fat, sugars and overall nutritional quality."
                                ));

                HealthCondition celiac =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "Celiac Disease",
                                        "An immune-mediated condition triggered by gluten. Foods containing "
                                                + "gluten-containing ingredients require special attention."
                                ));

                HealthCondition lactose =
                        healthConditionRepository.save(
                                new HealthCondition(
                                        "Lactose Intolerance",
                                        "A condition involving difficulty digesting lactose. Dairy and "
                                                + "lactose-containing ingredients may require attention."
                                ));

                ruleRepository.save(new HealthConditionRule(
                        diabetes,
                        "carbohydrates",
                        null,
                        null,
                        "g/100g",
                        "IMPORTANT_FACTOR",
                        "Review carbohydrate content",
                        "Carbohydrate intake directly affects blood glucose and should be considered "
                                + "when assessing packaged foods.",
                        "WHO",
                        "https://www.who.int/news-room/fact-sheets/detail/healthy-diet"
                ));

                ruleRepository.save(new HealthConditionRule(
                        diabetes,
                        "sugars",
                        null,
                        null,
                        "g/100g",
                        "IMPORTANT_FACTOR",
                        "Review sugar content",
                        "Free sugars are an important dietary consideration when assessing foods.",
                        "WHO",
                        "https://www.who.int/news-room/fact-sheets/detail/healthy-diet"
                ));

                ruleRepository.save(new HealthConditionRule(
                        hypertension,
                        "salt",
                        null,
                        null,
                        "g/100g",
                        "IMPORTANT_FACTOR",
                        "Review salt content",
                        "Reducing salt intake is an important dietary measure for cardiovascular health.",
                        "WHO",
                        "https://www.who.int/news-room/fact-sheets/detail/salt-reduction"
                ));

                ruleRepository.save(new HealthConditionRule(
                        cholesterol,
                        "saturatedFat",
                        null,
                        null,
                        "g/100g",
                        "IMPORTANT_FACTOR",
                        "Review saturated fat content",
                        "Saturated fat intake is an important factor when assessing cardiovascular dietary quality.",
                        "WHO",
                        "https://www.who.int/news-room/fact-sheets/detail/healthy-diet"
                ));

                ruleRepository.save(new HealthConditionRule(
                        obesity,
                        "energy",
                        null,
                        null,
                        "kJ/100g",
                        "IMPORTANT_FACTOR",
                        "Review energy density and serving size",
                        "Energy intake and portion size are important considerations in weight management.",
                        "WHO",
                        "https://www.who.int/news-room/fact-sheets/detail/obesity-and-overweight"
                ));

                ruleRepository.save(new HealthConditionRule(
                        heartDisease,
                        "saturatedFat",
                        null,
                        null,
                        "g/100g",
                        "IMPORTANT_FACTOR",
                        "Review saturated fat content",
                        "Saturated fat is an important dietary factor in cardiovascular health.",
                        "WHO",
                        "https://www.who.int/news-room/fact-sheets/detail/healthy-diet"
                ));

                ruleRepository.save(new HealthConditionRule(
                        heartDisease,
                        "salt",
                        null,
                        null,
                        "g/100g",
                        "IMPORTANT_FACTOR",
                        "Review salt content",
                        "High sodium/salt intake can be relevant to cardiovascular risk management.",
                        "WHO",
                        "https://www.who.int/news-room/fact-sheets/detail/salt-reduction"
                ));

                ruleRepository.save(new HealthConditionRule(
                        celiac,
                        "gluten",
                        "CONTAINS",
                        null,
                        null,
                        "HIGH_CONCERN",
                        "Avoid foods containing gluten",
                        "Celiac disease requires avoidance of gluten.",
                        "NHS",
                        "https://www.nhs.uk/conditions/coeliac-disease/"
                ));

                ruleRepository.save(new HealthConditionRule(
                        lactose,
                        "lactose",
                        "CONTAINS",
                        null,
                        null,
                        "HIGH_CONCERN",
                        "Review lactose-containing ingredients",
                        "Lactose-containing foods can cause symptoms in people with lactose intolerance.",
                        "NHS",
                        "https://www.nhs.uk/conditions/lactose-intolerance/"
                ));
            }

            if (additiveRepository.count() == 0) {

                additiveRepository.save(new Additive(
                        "E211",
                        "Sodium Benzoate",
                        "Preservative",
                        "A benzoate preservative used to inhibit microbial growth in certain foods.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E202",
                        "Potassium Sorbate",
                        "Preservative",
                        "A sorbate preservative used to inhibit mould and yeast growth.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E621",
                        "Monosodium Glutamate",
                        "Flavour Enhancer",
                        "A flavour enhancer commonly used to provide or intensify umami taste.",
                        "REGULATED",
                        "GMP",
                        null,
                        null,
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E951",
                        "Aspartame",
                        "Sweetener",
                        "An intense sweetener used in a range of reduced-sugar or sugar-free foods.",
                        "REGULATED",
                        "ADI",
                        40.0,
                        "mg/kg bw/day",
                        null,
                        "JECFA",
                        "FAO/WHO JECFA",
                        "https://www.fao.org/food-safety/scientific-advice/jecfa/en/"
                ));

                additiveRepository.save(new Additive(
                        "E950",
                        "Acesulfame Potassium",
                        "Sweetener",
                        "An intense sweetener used in food and beverage products.",
                        "REGULATED",
                        "ADI",
                        15.0,
                        "mg/kg bw/day",
                        null,
                        "JECFA",
                        "FAO/WHO JECFA",
                        "https://www.fao.org/food-safety/scientific-advice/jecfa/en/"
                ));

                additiveRepository.save(new Additive(
                        "E955",
                        "Sucralose",
                        "Sweetener",
                        "An intense sweetener used in reduced-sugar and sugar-free products.",
                        "REGULATED",
                        "ADI",
                        15.0,
                        "mg/kg bw/day",
                        null,
                        "JECFA",
                        "FAO/WHO JECFA",
                        "https://www.fao.org/food-safety/scientific-advice/jecfa/en/"
                ));

                additiveRepository.save(new Additive(
                        "E954",
                        "Saccharin",
                        "Sweetener",
                        "An intense sweetener used in some low-calorie food products.",
                        "REGULATED",
                        "ADI",
                        5.0,
                        "mg/kg bw/day",
                        null,
                        "JECFA",
                        "FAO/WHO JECFA",
                        "https://www.fao.org/food-safety/scientific-advice/jecfa/en/"
                ));

                additiveRepository.save(new Additive(
                        "E250",
                        "Sodium Nitrite",
                        "Preservative",
                        "A curing preservative used in certain meat products.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E251",
                        "Sodium Nitrate",
                        "Preservative",
                        "A nitrate preservative used in selected food applications.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E320",
                        "BHA",
                        "Antioxidant",
                        "Butylated hydroxyanisole, an antioxidant used to help prevent oxidation.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex",
                        "https://www.fao.org/4/y2774e/y2774e06.htm"
                ));

                additiveRepository.save(new Additive(
                        "E321",
                        "BHT",
                        "Antioxidant",
                        "Butylated hydroxytoluene, an antioxidant used to help prevent oxidation.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex",
                        "https://www.fao.org/4/y2774e/y2774e06.htm"
                ));

                additiveRepository.save(new Additive(
                        "E220",
                        "Sulfur Dioxide",
                        "Preservative",
                        "A sulfite preservative used in selected food applications.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E102",
                        "Tartrazine",
                        "Colour",
                        "A synthetic yellow food colour used in selected food categories.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E110",
                        "Sunset Yellow FCF",
                        "Colour",
                        "A synthetic yellow-orange food colour used in selected food categories.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));

                additiveRepository.save(new Additive(
                        "E129",
                        "Allura Red AC",
                        "Colour",
                        "A synthetic red food colour used in selected food categories.",
                        "REGULATED",
                        "MAXIMUM_USE_LEVEL",
                        null,
                        "mg/kg",
                        null,
                        "CODEX",
                        "Codex GSFA",
                        "https://codex.fao.org/codex-texts/codex-online-databases/gsfa/"
                ));
            }
        };
    }
}