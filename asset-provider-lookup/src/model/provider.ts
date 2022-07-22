export class Provider {
    id: number = 0;
    providerName: string = "";
    logoUrl: string = "";
    redirectUrl: string = "";
}

export class AssetLookup {
    customerId: number = 0;
    providers:  Array<Provider> = new Array<Provider>();


}
