import { Component, OnInit } from '@angular/core';
import { ExcategoryModel } from '../../models/excategory.model';
import { ExcategoryService } from '../../services/excategory.service';
import { Observable } from 'rxjs/index';
import {Settings} from "../../settings";

@Component({
  selector: 'app-excategory',
  templateUrl: './excategory.component.html',
  styleUrls: ['./excategory.component.css']
})
export class ExcategoryComponent implements OnInit {
  private list: Observable<ExcategoryModel[]> = this.service.getTopCategories();
  private cities: Observable<ExcategoryModel[]>;
  private serverURL = Settings.URL+ '/category/img?link=';


  constructor(public service: ExcategoryService) {
  }

  ngOnInit() {
    this.list = this.service.getTopCategories();
  }

  getCities(nextLevel: String) {
    this.cities = this.service.getCategoriesByNextLevel(nextLevel);
  }

}
