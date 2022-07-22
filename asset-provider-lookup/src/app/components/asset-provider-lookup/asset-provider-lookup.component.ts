import { Component, OnInit, ViewChild } from '@angular/core';
import { AssetProviderServiceService } from 'src/app/services/asset-provider-service.service';
import { FormControl, Validators } from '@angular/forms';
import { AssetLookup, Provider } from 'src/model/provider';
@Component({
  selector: 'app-asset-provider-lookup',
  templateUrl: './asset-provider-lookup.component.html',
  styleUrls: ['./asset-provider-lookup.component.css']
})
export class AssetProviderLookupComponent implements OnInit {

  trailerFlag: number = 0;
  data = { trailer: "" }
  customerName: any = "";
  assetId: string = "";
  assetFormControl = new FormControl('', [Validators.required]);
  constructor(private assetProviderLookupService: AssetProviderServiceService) { } // make variable private so that it would be accessible through out the component
  provider: Provider = new Provider();
  assetLookup: AssetLookup = new AssetLookup();

  ngOnInit() {
     //var url = "http://nussbaum.trackmytrailer.net";
    // var url ="http://qa-pctutils-env.eba-24umxsue.us-east-1.elasticbeanstalk.com/";
    // console.log("window.location.href>>  "+window.location.href);
    // console.log("window.location  "+window.location);
    var url = window.location.href;
   // var url = window.location.href.split('/').toString();
    console.log("ngOninit url " + url);
    this.customerName = (url.split('.')[0].split('//').pop());
    console.log("this.customerName " + this.customerName);
    this.getCustomerDetails(this.customerName);
  }

  getCustomerDetails(customerName: string) {
    this.assetProviderLookupService.findCustomerDetails(customerName)
      .subscribe(data => {
        console.log("data " + data);
        this.assetLookup = data.body;
        console.log(" getCustomerDetails " + JSON.stringify(this.assetLookup));
      },
        error => {
          console.log("getCustomerDetails error " + error);

        }
      );
  }

  getAssetProviderDetails(customerId: number, assetId: string) {

    this.assetProviderLookupService.getAsset(customerId, assetId).subscribe(data => {
      this.provider = data.body;
      console.log("this.provider" + JSON.stringify(this.provider));
      if (this.provider != null) {
        this.trailerFlag = 1;
      }
    },
      error => {
        this.trailerFlag = 2;
        console.log("trailerFlag >> " + this.trailerFlag);
        this.provider
        console.log(" this.provider >> " + JSON.stringify(this.provider));

        console.log("error >> " + JSON.stringify(error));
      }
    );
  }


  doSubmitForm() {

    this.assetId = this.data.trailer;

    this.getAssetProviderDetails(this.assetLookup.customerId, this.assetId);
    this.data.trailer = '';
  }

  getProviderName() {
    return this.provider.providerName;
  }
  handleClear() {
    this.assetFormControl.reset();
  }
}


