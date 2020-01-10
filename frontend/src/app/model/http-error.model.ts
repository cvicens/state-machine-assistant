export class HttpRuntimeException {
    constructor(
        public error: string,
        public status: number) { }
}
