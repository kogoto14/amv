import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(relativeTime);

export default class DateUtils {
  static date(date: string | undefined) {
    return this.format(date, 'YYYY/MM/DD');
  }

  static datetime(date: string | undefined) {
    return this.format(date, 'YYYY/MM/DD HH:mm');
  }

  static relative(date: string | undefined) {
    return date ? dayjs(date).fromNow() : '';
  }

  private static format(date: string | undefined, format: string) {
    return date ? dayjs(date).format(format) : '';
  }

  static formatSeconds(ms?: number) {
    if (ms == null) {
      return '-';
    }
    const seconds = ms / 1000;
    const formatted = seconds >= 10 ? Math.round(seconds).toString() : seconds.toFixed(1);
    return `${formatted} sec`;
  }
}
