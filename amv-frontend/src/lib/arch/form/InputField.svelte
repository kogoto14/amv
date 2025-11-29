<script lang="ts">
  import { ValidationMessage } from '@felte/reporter-svelte';

  interface Props {
    id: string;
    label: string;
    labelPos?: 'before' | 'after';
    type?: string;
    value: any;
    disabled?: boolean;
    min?: number | string;
    max?: number | string;
    step?: number | string;
    width?: string;
  }

  let {
    id,
    label,
    labelPos = 'before',
    type = 'text',
    value = $bindable(),
    disabled = false,
    min,
    max,
    step,
    width
  }: Props = $props();

  function setType(node: HTMLInputElement) {
    node.type = type;
  }
</script>

{#if labelPos === 'before'}
  <label for={id}>{label}</label>
{/if}
<ValidationMessage for={id} let:messages>
  <fieldset>
    <input
      {id}
      name={id}
      use:setType
      bind:value
      aria-describedby="invalid-{id}"
      {disabled}
      {min}
      {max}
      {step}
      style:width
    />
    {#if labelPos === 'after'}
      <label for={id}>{label}</label>
    {/if}
    <small id="invalid-{id}">{messages || ''}</small>
  </fieldset>
</ValidationMessage>

<style lang="scss">
  input {
    margin-inline-end: 0.5rem;
  }

  label {
    display: inline-block;
    white-space: nowrap;
  }
</style>
