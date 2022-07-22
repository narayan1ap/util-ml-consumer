import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AssetProviderServiceService {
///  private base_url: string;
  
public baseUrl = environment.baseUrl;

  constructor(private http: HttpClient) {   
  // baseUrl:'http://pctutils-qa.eba-cmmspirc.us-east-1.elasticbeanstalk.com'

   // this.base_url = 'http://qa-pctutils-env.eba-24umxsue.us-east-1.elasticbeanstalk.com'
  // this.base_url = 'http://localhost:8080/asset-lookup/'

  }
   public findCustomerDetails(customerName:any) {
        console.log("customerName "+ customerName);
      return this.http.get<any>(this.baseUrl +"/asset-lookup/"+ customerName);
   }
   public getAsset(customerId :number ,assetId : string){
    console.log("customerId "+ customerId + " assetId "+assetId);
     return this.http.get<any>(this.baseUrl  +"/asset-lookup/"+ 'asset?customerId='+customerId+'&assetId='+assetId)
   }
}
