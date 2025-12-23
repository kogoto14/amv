<script lang="ts">
  import { messageStore } from '$lib/arch/global/MessageStore';
  import '@picocss/pico/css/pico.min.css';
  import '@picocss/pico/css/pico.colors.min.css';
  import * as m from '$lib/paraglide/messages';
  import { page } from '$app/state';
  import { env } from '$env/dynamic/public';
  import {
    ArrowRightLeft,
    Braces,
    Database,
    FileType,
    House,
    ListTree,
    Network,
    Table2,
    type Icon as IconType
  } from '@lucide/svelte';
  interface Props {
    children?: import('svelte').Snippet;
  }

  let { children }: Props = $props();

  type MenuItem = {
    id: string;
    name: string;
    href: string;
    icon: typeof IconType;
  };

  const baseUrl = env.PUBLIC_BACKEND_URL || new URL(window.location.href).origin;

  const menuItems: MenuItem[] = [
    { id: 'top', name: 'Top', href: '/', icon: House },
    { id: 'type', name: 'Types', href: '/types', icon: FileType },
    { id: 'call-hierarchy', name: 'Call Hierarchy', href: '/diagrams/call-tree', icon: ListTree },
    {
      id: 'interaction',
      name: 'Interaction',
      href: '/documents/interaction',
      icon: ArrowRightLeft
    },
    { id: 'structure', name: 'Structure', href: '/diagrams/class', icon: Network },
    { id: 'crud', name: 'CRUD', href: '/diagrams/crud', icon: Table2 },
    {
      id: 'internal-db',
      name: 'Internal DB',
      href: `${baseUrl}/h2`,
      icon: Database
    },
    { id: 'api', name: 'API', href: `${baseUrl}/q/swagger-ui/`, icon: Braces }
  ];
</script>

<svelte:head>
  <title>{page.data.title} - Application Metadata Visualizer</title>
</svelte:head>

<nav class="container-fluid">
  <ul>
    <li><strong>Application Metadata Visualizer</strong></li>
  </ul>
  <ul>
    {#each menuItems as item}
      {@const current = page.url.pathname === item.href}
      <li>
        <a class={current ? 'primary' : 'contrast'} id={item.id} href={item.href}
          ><span title={item.name}><item.icon class="icon" /></span> <span>{item.name}</span></a
        >
      </li>
    {/each}
  </ul>
</nav>

<main class="container-fluid">
  <h1>{page.data.title}</h1>

  {#if $messageStore.display}
    <article class="message">
      <span id="globalMessage"> {$messageStore.text}</span>
      <button class="close" aria-label="close" onclick={() => messageStore.hide()}></button>
    </article>
  {/if}
  {@render children?.()}
</main>

<style lang="scss">
  @use '../app.scss';

  :global(:root),
  :global(:host) {
    --font-size-step: 0.0625em;
    --font-size-sm: 0.8125em;
    --font-size-md: calc(var(--font-size-sm) + var(--font-size-step));
    --font-size-lg: calc(var(--font-size-md) + var(--font-size-step));
    --font-size-xl: calc(var(--font-size-lg) + var(--font-size-step));
    --font-size-2xl: calc(var(--font-size-xl) + var(--font-size-step));

    @media (min-width: app.$screen-sm) {
      --pico-font-size: var(--font-size-sm);
    }
    @media (min-width: app.$screen-md) {
      --pico-font-size: var(--font-size-md);
    }
    @media (min-width: app.$screen-lg) {
      --pico-font-size: var(--font-size-lg);
    }

    @media (min-width: app.$screen-xl) {
      --pico-font-size: var(--font-size-xl);
    }

    @media (min-width: app.$screen-2xl) {
      --pico-font-size: var(--font-size-2xl);
    }
  }

  :global {
    .del-color {
      color: var(--pico-del-color);
    }

    table.list {
      width: 100%;

      th {
        text-align: center;
        white-space: nowrap;
      }
    }
    svg.lucide-icon {
      width: 1.2em;
      height: 1.2em;
    }

    .required {
      color: var(--pico-form-element-invalid-focus-color);
      margin-left: 0.2em;
    }

    section.setting {
      text-align: center;

      fieldset {
        display: inline-block;
        margin-inline-end: 1rem;
        width: unset;
      }
    }
  }

  nav {
    a > span:nth-child(2) {
      display: none;
    }

    @media (min-width: app.$screen-lg) {
      a > span:nth-child(2) {
        display: inline;
      }
    }
  }

  article.message {
    display: flex;
    justify-content: space-between;
  }

  button.close {
    background-image: var(--pico-icon-close);
    background-color: transparent;
    border: none;
    background-size: auto 1rem;
  }
</style>
