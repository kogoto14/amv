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
          <span><StatusIcon done={!!codebase.status?.checkedOut} /> Checked Out</span>
          <span><StatusIcon done={!!codebase.status?.projectsLoaded} /> Projects Loaded</span>
          <span><StatusIcon done={!!codebase.status?.metadataExtracted} /> Metadata Extracted</span>
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
            <CalendarArrowDown /> 2 days ago
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
      <h5><BookDown /> 新規登録</h5>
      <p>AMVでソースコードを可視化するには、リポジトリをAMVに登録します。</p>
      <p>
        <a id="addNewCodebase" href="/codebases/new"> 新しくリポジトリを登録する</a>
      </p>
      <h5><BookDown /> デモ</h5>
      <p>AMVの動作を確認したい場合は、AMV自体のリポジトリを登録します。</p>
      <p>
        <a id="addAmvCodebase" href="/codebases/new?url=https://github.com/project-au-lait/amv.git">
          AMVのリポジトリを登録する
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
