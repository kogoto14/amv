# Application Metadata Visualizer

[日本語](README_ja.md)

Application Metadata Visualizer (AMV) parses the source code of Java applications in Git repositories to extract metadata and visualize the internal processing of applications.

![Overview](overview.png)

## Usage

### Required Software

Docker is required to use AMV.

### Execution Steps

1. Run the following command:

```sh
# Windows (Command Prompt)
docker run -d --name amv -p 8080:8080 -v %USERPROFILE%/.m2:/home/jboss/.m2 projectaulait/amv

# Windows (PowerShell)
docker run -d --name amv -p 8080:8080 -v ${HOME}/.m2:/home/jboss/.m2 projectaulait/amv

# Mac / Linux
docker run -d --name amv -p 8080:8080 -v ~/.m2:/home/jboss/.m2 projectaulait/amv
```

2. Open http://localhost:8080 in your browser.
3. Register the Git repository you want to visualize and execute the analysis.

#### Notes

- **Why mount the `.m2` directory?**
  When the target Git repository is a Maven project, AMV executes a Maven build (`mvn package dependency:build-classpath`). During the Maven build, project dependencies are downloaded. By mounting the `.m2` directory from the host, you can potentially reduce the time required for these downloads.
  If the target is a Gradle project, mount `.gradle/caches` under the user home for the same reason.
- **Accessing Git repositories via VPN**
  If VPN connection is required to clone the target Git repository, follow these steps for analysis:
  1. Clone as a mirror repository on the host PC
  2. Mount the cloned mirror repository when running AMV
  3. Register the container path of the mounted Git repository in AMV and perform analysis

  **Example**
  ```sh
  # ⅰ
  git clone --mirror https://server/myrepo.git

  docker run -v ./:/mnt/amv [followed by the same docker command as in execution steps]
  ```

  In the above example, the path to register in AMV would be `/mnt/amv/myrepo.git`.

- **Log output destination**
  AMV logs are output to:
  - Container standard output (INFO level)
  - Log file inside the container `/deployments/logs/amv.log` (DEBUG level)

## Building from Source Code

### Required Software

The following software is required to build and run AMV from source code:

- Git
- Docker
- Java v21+
- Node.js v22+
- pnpm
- VSCode

### Build

1. Run the following commands:

```sh
git clone https://github.com/project-au-lait/amv.git
cd amv

# H2 mode
./mvnw install -P setup -T 1C

# PostgreSQL mode
export AMV_DB=postgres
./mvnw install -P setup -T 1C
```

### Verification

1. Run VSCode Tasks `start-backend` and `start-frontend`.
2. In the displayed browser, perform the following operations:
   1. `Register demo codebase`
   2. `Register`
   3. `Analyze`
