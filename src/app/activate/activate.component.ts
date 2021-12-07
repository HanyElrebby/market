import { Component, OnInit } from '@angular/core';
import {ActivateService} from "./activate.service";

@Component({
  selector: 'app-activate',
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.css']
})
export class ActivateComponent implements OnInit {
  public success = false;
  constructor(private activateService: ActivateService) { }

  ngOnInit(): void {
  }

  public init(key: string): void {
    this.activateService
      .activateAccount(key)
      .then(
        res => {
          this.success = true;
        },
        err => {
          this.success = false;
        }
      );
  }

}
