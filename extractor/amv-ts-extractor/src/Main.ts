import { Project } from "ts-morph";
import * as path from "path";
import { MetadataExtractor } from "./MetadataExtractor";
import * as fs from "fs";
import { ClassModel } from "./Models";

const rootDir =
  "/Users/yuichi_kuwahara/Workspace/paygood/paygood-eats/paygood-eats-user/paygood-eats-user-front";
const projectDir = "paygood-eats-user-front";

const OUTPUT_DIR = path.resolve(__dirname, "../output");

const extractor = new MetadataExtractor(rootDir, projectDir);

const project = new Project({
  tsConfigFilePath: path.resolve(rootDir, "tsconfig.json"),
});

const srcFiles = project.getSourceFiles(
  `${rootDir}/domains/**/FacilityDataManager.ts`
);

console.log(`Found ${srcFiles.length} source files.`);

for (const sourceFile of srcFiles) {
  const sourceFileModel = extractor.extractSrcFile(sourceFile);
  const classes = sourceFileModel.classes;

  console.log(
    `Found ${classes.length} classes in ${sourceFile.getFilePath()}.`
  );

  write(sourceFile.getFilePath(), classes);
}

function write(sourceFilePath: string, classes: ClassModel[]) {
  const relativePath = path.relative(rootDir, sourceFilePath);

  const outputDir = path.join(
    OUTPUT_DIR,
    projectDir,
    path.dirname(relativePath)
  );

  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true });
  }

  const outputPath = path.join(
    outputDir,
    path.basename(sourceFilePath) + ".json"
  );

  console.log(`Writing to ${outputPath}`);

  fs.writeFileSync(outputPath, JSON.stringify(classes, null, 2));
}
