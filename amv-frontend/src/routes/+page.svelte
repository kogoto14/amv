<script lang="ts">
  import {
    BookDown,
    BookMarked,
    CalendarArrowDown,
    Folder,
    GitBranch,
    GitCommitVertical,
    Timer
  } from '@lucide/svelte';
  import StatusIcon from '$lib/components/StatusIcon.svelte';
  import DateUtils from '$lib/arch/util/DateUtils';
  import type { PageProps } from './$types';
  import * as m from '$lib/paraglide/messages';

  let { data }: PageProps = $props();
  let { codebases } = $derived(data);

  const shortCommit = (hash?: string) => (hash ? hash.substring(0, 7) : '-');
</script>

<section>
  <div class="codebase grid">
    {#each codebases as codebase}
      <article>
        <h5>
          <BookMarked />
          <a href={`/codebases/${codebase.id}`}>{codebase.name}</a>
        </h5>
        <p class="status">
          <span><StatusIcon done={!!codebase.status?.checkedOut} /> {m.checkedOut()}</span>
          <span><StatusIcon done={!!codebase.status?.projectsLoaded} /> {m.projectsLoaded()}</span>
          <span
            ><StatusIcon done={!!codebase.status?.metadataExtracted} />
            {m.metadataExtracted()}</span
          >
        </p>
        <p>
          <span>
            <GitBranch />
            {codebase.branch ?? '-'}
          </span>
          <span title={codebase.commitHash ?? ''}>
            <GitCommitVertical />
            {shortCommit(codebase.commitHash)}
          </span>
          <span title="2025/03/10 12:00:00">
            <CalendarArrowDown /> 3 days ago
          </span>
          <span>
            <Timer />
            {DateUtils.formatSeconds(codebase.analyzedIn)}
          </span>
        </p>
        <p class="projects grid">
          {#each codebase.projects as project, i}
            <span>
              <Folder />
              {project.name}
            </span>
          {/each}
        </p>
      </article>
    {/each}
    <article>
      <h5><BookDown /> {m.register()}</h5>
      <p>{m.registerDescription()}</p>
      <p>
        <a id="addNewCodebase" href="/codebases/new"> {m.registerNewRepository()}</a>
      </p>
      <h5><BookDown /> {m.demo()}</h5>
      <p>{m.demoDescription()}</p>
      <p>
        <a id="addAmvCodebase" href="/codebases/new?url=https://github.com/project-au-lait/amv.git">
          {m.registerAmvRepository()}
        </a>
      </p>
    </article>
  </div>
</section>

<style lang="scss">
  @use '../app.scss';
  .codebase {
    grid-template-columns: 1fr;

    article p span {
      margin-right: 1em;
    }
  }

  .projects {
    grid-template-columns: 1fr 1fr;
    grid-column-gap: calc(var(--pico-grid-column-gap) / 2);
    grid-row-gap: calc(var(--pico-grid-row-gap) / 2);
  }

  @media (min-width: app.$screen-md) {
    .codebase {
      grid-template-columns: 1fr 1fr;
    }

    .projects {
      grid-template-columns: 1fr 1fr 1fr;
    }
  }

  @media (min-width: app.$screen-xl) {
    .projects {
      grid-template-columns: 1fr 1fr 1fr 1fr;
    }
  }
</style>
