import string
import random

from locust import HttpUser, task, between

class QuickstartUser(HttpUser):
    wait_time = between(1, 2)
    device_id_list = []
    os_types = ["PC_WEB", "MOBILE_WEB", "IOS", "ANDROID"]
    platform_types = ["PC_WEB", "MOBILE_WEB", "MOBILE_APP"]

    @task
    def hello_world(self):
        self.client.get(f"/cms/area/{random.randint(1,2)}?platformType={random.choice(self.platform_types)}&osType={random.choice(self.os_types)}")
